import mpi.MPI;
import mpi.Status;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class BufferBlocking {
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

                int[] sendBuffer1 = Arrays.copyOfRange(a, start, start + step);
                ByteBuffer byteBuffer1 = ByteBuffer.allocate(sendBuffer1.length * 10);
                MPI.Buffer_attach(byteBuffer1);
                MPI.COMM_WORLD.Bsend(sendBuffer1, 0, sendBuffer1.length, MPI.INT, i, TAG);
                MPI.Buffer_detach();


                int[] sendBuffer2 = Arrays.copyOfRange(b, start, start + step);
                ByteBuffer byteBuffer2 = ByteBuffer.allocate(sendBuffer2.length * 10);
                MPI.Buffer_attach(byteBuffer2);
                MPI.COMM_WORLD.Bsend(sendBuffer2, 0, sendBuffer2.length, MPI.INT, i, TAG);
                MPI.Buffer_detach();
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
            System.out.println("BufferBlocking result: " + (result == Utils.vectorsMultiplication(a, b)));
            long time_finish = System.currentTimeMillis();
            System.out.println("BufferBlocking time: " + ((double) time_finish - time_start));
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

            ByteBuffer byteBuffer = ByteBuffer.allocate(result.length * 100);
            MPI.Buffer_attach(byteBuffer);
            MPI.COMM_WORLD.Bsend(result, 0, result.length, MPI.INT, 0, TAG);
            MPI.Buffer_detach();
        }
        MPI.Finalize();
    }
}
