/*
 Спросить про нечетный пример.
 */


import mpi.MPI;

public class Scatter {
    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int root = 0;

        int N = 30;
        int k = N / size + (N % size != 0 ? 1 : 0);

        int[] a = Utils.getRandomArray(N);
        int[] b = Utils.getRandomArray(N);

        int start = getStartIndex(rank, k);
        int step = getStep(start, k, N);

        int[] recvA = new int[step];
        int[] recvB = new int[step];

        MPI.COMM_WORLD.Scatter(a, 0, step, MPI.INT, recvA, 0, step, MPI.INT, root);
        MPI.COMM_WORLD.Scatter(b, 0, step, MPI.INT, recvB, 0, step, MPI.INT, root);

        int[] result = {0};
        for (int i = 0; i < step; i++) {
            result[0] += recvA[i] * recvB[i];
        }

        int[] gather = new int[size];
        MPI.COMM_WORLD.Gather(result, 0, result.length, MPI.INT, gather, 0, 1, MPI.INT, root);

        if (rank == root) {
            int[] globalResult = new int[1];
            for (int i = 0; i < size; i++) {
                globalResult[0] += gather[i];
            }
            System.out.println("Result: " + (globalResult[0] == Utils.vectorsMultiplication(a, b)));
        }

    }

    private static int getStartIndex(int rank, int k) {
        return rank * k;
    }

    private static int getStep(int start, int k, int N) {
        int end = start + k;
        if (N < end) {
            end = N;
        }
        return end - start;
    }
}
