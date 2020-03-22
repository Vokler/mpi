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
        Request[] reqs = new Request[threadsCount];
        Status st;

        int graphSize = 1000;
        int arraySize = graphSize * graphSize;
        int[][] graph = Utils.createGraph(graphSize);
        int[] array = Utils.matrixInArray(graph);
        int k = arraySize / threadsCount + (arraySize % threadsCount != 0 ? 1 : 0);

        long startTimeForOneProc = System.currentTimeMillis();
        int countEdgesInOneProc = Utils.countEdges(graph);
        long endTimeForOneProc = System.currentTimeMillis();

        if (rank == root) {
            long startTimeForManyProc = System.currentTimeMillis();
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
            long endTimeForManyProc = System.currentTimeMillis();

            boolean check = (countEdgesInOneProc == countEdgesInManyProc);
            System.out.println("Result: " + check);
            System.out.println("Work time for one proc: " + (endTimeForOneProc - startTimeForOneProc));
            System.out.println("Work time for many proc: " + (endTimeForManyProc - startTimeForManyProc));

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
    }
}
