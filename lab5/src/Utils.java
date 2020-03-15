import java.util.Random;

public class Utils {
    public static int randomInt(int min, int max) {
        int diff = max - min;
        Random random = new Random();
        int num = random.nextInt(diff + 1);
        num += min;
        return num;
    }

    static int getStartIndex(int i, int k) {
        return (i - 1) * k;
    }

    static int getStep(int start, int k, int N) {
        int end = start + k;
        if (N < end) {
            end = N;
        }
        return end - start;
    }

    static int vectorsMultiplication(int[] a, int[] b) {
        int result = 0;
        if (a.length == b.length) {
            int length = a.length;
            for (int i = 0; i < length; i++) {
                result += a[i] * b[i];
            }
        }
        return result;
    }
}
