import mpi.MPI;

import java.util.Arrays;

public class GraphBC {
    public static void main(String[] args) {
        MPI.Init(args);
        int root = 0;
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int threadsCount = size - 1;

        int graphSize = 300;
        int arraySize = graphSize * graphSize;
        int[][] graph = Utils.createGraph(graphSize);
        int[] array = Utils.matrixInArray(graph);
        int k = arraySize / threadsCount + (arraySize % threadsCount != 0 ? 1 : 0);

        long startTimeForOneProc = System.currentTimeMillis();
        int countEdgesInOneProc = Utils.countEdges(graph);
        long endTimeForOneProc = System.currentTimeMillis();

        long startTimeForManyProc = System.currentTimeMillis();
        MPI.COMM_WORLD.Bcast(array, 0, array.length, MPI.INT, 0);

        int startIdx = Utils.getStartIndex(rank, k);
        int step = Utils.getStep(startIdx, k, arraySize);
        int stopIdx = startIdx + step;

        int[] recvArray = Arrays.copyOfRange(array, startIdx, stopIdx);

        int[] result = {0};
        for (int el : recvArray) {
            if ((rank != root) && (el == 1)) {
                result[0] += el;
            }
        }

        int[] globalResult = new int[1];
        MPI.COMM_WORLD.Reduce(result, 0, globalResult, 0, 1, MPI.INT, MPI.SUM, 0);
        long endTimeForManyProc = System.currentTimeMillis();

        if (rank == root) {

            long resultTimeForOneProc = endTimeForOneProc - startTimeForOneProc;
            long resultTimeForManyProc = endTimeForManyProc - startTimeForManyProc;

            int countEdgesInManyProc = globalResult[0];
            boolean check = (countEdgesInOneProc == countEdgesInManyProc);
            System.out.println("Result: " + check);
            System.out.println("Work time for one proc: " + resultTimeForOneProc);
            System.out.println("Work time for many proc: " + resultTimeForManyProc);
        }

        MPI.Finalize();
    }
}
