import mpi.MPI;
import mpi.Request;
import mpi.Status;

public class Graph {
    public static void main(String[] args) {
        MPI.Init(args);
        int root = 0;
        int count, TAG = 0;
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int threadsCount = size - 1;
        long startTimeForManyProc = 0;
        Request[] reqs = new Request[threadsCount];
        Status st;

//        int[][] graph = Utils.createFixedGraph();
        int[][] graph = Utils.createGraph(1000);
        int graphSize = graph.length;
        int arraySize = graphSize * graphSize;
        int[] array = Utils.matrixInArray(graph);
        int k = arraySize / threadsCount + (arraySize % threadsCount != 0 ? 1 : 0);

        long startTimeForOneProc = System.currentTimeMillis();
        boolean isHypercubeGraph = Utils.isHypercubeGraph(graph);
        long endTimeForOneProc = System.currentTimeMillis();

        if (rank == root) {
            startTimeForManyProc = System.currentTimeMillis();
            for (int i = 1; i <= threadsCount; i++) {
                int start = Utils.getStartIndex(i, k);
                int step = Utils.getStep(start, k, arraySize);
                MPI.COMM_WORLD.Isend(array, start, step, MPI.INT, i, TAG);
            }

            int[] messageRecv = new int[threadsCount];
            for (int i = 1; i <= threadsCount; i++) {
                int index = i - 1;
                reqs[index] = MPI.COMM_WORLD.Irecv(messageRecv, index, 1, MPI.INT, i, TAG);
            }
            Request.Waitall(reqs);

            int countEdgesInManyProc = 0;
            for (int el : messageRecv) {
                countEdgesInManyProc += el;
            }

            /*
                Подсчитали кол-во ребер, теперь проверяем три условия на Гиперкуб:
                1. Граф является Регулярным;
                2. Кол-во вершин == 2^n;
                3. Кол-во ребер == 2^(n-1)*n;
            */

            int degree = Utils.getGraphDegree(graph);
            boolean firstCond = (countEdgesInManyProc == (degree * graph.length));
            boolean secondCond = (graph.length == Math.pow(2, degree));
            boolean thirdCond = (Utils.countRegularGraphEdges(graph) == (Math.pow(2, degree - 1) * degree));

            if (!firstCond || !secondCond || !thirdCond) {
                System.out.println("Graph is not Hypercube.");
            } else {
                System.out.println("Graph is Hypercube.");
            }

        } else {
            st = MPI.COMM_WORLD.Probe(0, TAG);
            count = st.Get_count(MPI.INT);
            int[] messageRecv = new int[count];
            MPI.COMM_WORLD.Recv(messageRecv, 0, count, MPI.INT, root, TAG);

            int[] result = {0};
            for (int el : messageRecv) {
                if (el == 1) {
                    result[0] += el;
                }
            }
            MPI.COMM_WORLD.Isend(result, 0, 1, MPI.INT, root, TAG);
        }

        if (rank == root) {
            long endTimeForManyProc = System.currentTimeMillis();
//            System.out.println("Work time for one proc: " + (endTimeForOneProc - startTimeForOneProc));
            System.out.println("Work time for many proc: " + (endTimeForManyProc - startTimeForManyProc));
        }
    }
}
