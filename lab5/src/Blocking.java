/*
 Скалярное проивзденеи вектора a и b в блокирующем режиме.
 */

import mpi.MPI;
import mpi.Status;


public class Blocking {

    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int count, TAG = 0;
        Status st;

        int N = 10000;
        int threadsCount = size - 1;
        int k = N / threadsCount + (N % threadsCount != 0 ? 1 : 0);

        if (rank == 0) {
            long time_start = System.currentTimeMillis();
            int[] a = new int[N];
            int[] b = new int[N];

            for (int i = 0; i < N; i++) {
                a[i] = Utils.randomInt(1, 10);
                b[i] = Utils.randomInt(1, 10);
            }

            for (int i = 1; i <= threadsCount; i++) {
                int start = Utils.getStartIndex(i, k);
                int step = Utils.getStep(start, k, N);
                MPI.COMM_WORLD.Send(a, start, step, MPI.INT, i, TAG);
                MPI.COMM_WORLD.Send(b, start, step, MPI.INT, i, TAG);
            }

            int[] messageRecv = new int[threadsCount];
            for (int i = 1; i <= threadsCount; i++) {
                MPI.COMM_WORLD.Recv(messageRecv, i - 1, 1, MPI.INT, i, TAG);
            }

            int result = 0;
            for (int i : messageRecv) {
                result += i;
            }
            System.out.println("========");
            System.out.println("Blocking result: " + (result == Utils.vectorsMultiplication(a, b)));
            long time_finish = System.currentTimeMillis();
            System.out.println("Blocking time: " + ((double) time_finish - time_start));

        } else {
            st = MPI.COMM_WORLD.Probe(0, TAG);
            count = st.Get_count(MPI.INT);
            int[] firstMessageRecv = new int[count];
            int[] secondMessageRecv = new int[count];

            MPI.COMM_WORLD.Recv(firstMessageRecv, 0, count, MPI.INT, 0, TAG);
            MPI.COMM_WORLD.Recv(secondMessageRecv, 0, count, MPI.INT, 0, TAG);

            int[] result = {0};
            for (int i = 0; i < count; i++) {
                result[0] += firstMessageRecv[i] * secondMessageRecv[i];
            }
            MPI.COMM_WORLD.Send(result, 0, 1, MPI.INT, 0, TAG);

        }

        MPI.Finalize();
    }
}
