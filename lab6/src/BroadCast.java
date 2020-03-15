import mpi.MPI;

import java.util.Arrays;

public class BroadCast {
    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int root = 0;


        int N = 1000;
        int threadsCount = size - 1;
        int k = N / threadsCount + (N % threadsCount != 0 ? 1 : 0);

        int[] a = Utils.getRandomArray(N);
        int[] b = Utils.getRandomArray(N);

        long startTime = System.currentTimeMillis();

        MPI.COMM_WORLD.Bcast(a, 0, a.length, MPI.INT, 0);
        MPI.COMM_WORLD.Bcast(b, 0, a.length, MPI.INT, 0);

        int start = getStartIndex(rank, k);
        int step = getStep(start, k, N);
        int count = start + step;

        int[] recvA = Arrays.copyOfRange(a, start, count);
        int[] recvB = Arrays.copyOfRange(b, start, count);

        int arrSize = recvA.length; // doesn't matter recvA or recvB (they have same size)

        int[] result = {0};
        for (int i = 0; i < arrSize; i++) {
            if (rank != root) {
                result[0] += recvA[i] * recvB[i];
            }
        }

        int[] globalResult = new int[1];
        MPI.COMM_WORLD.Reduce(result, 0, globalResult, 0, 1, MPI.INT, MPI.SUM, 0);
        if (rank == 0) {
            long endTime = System.currentTimeMillis();
            System.out.println("Work time: " + (endTime - startTime));
            System.out.println("Result: " + (globalResult[0] == Utils.vectorsMultiplication(a, b)));
        }

        MPI.Finalize();
    }

    private static int getStartIndex(int rank, int k) {
        if (rank != 0) {
            return (rank - 1) * k;
        }
        return 0;
    }

    private static int getStep(int start, int k, int N) {
        int end = start + k;
        if (N < end) {
            end = N;
        }
        return end - start;
    }
}
