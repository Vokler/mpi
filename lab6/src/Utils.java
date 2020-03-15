import java.util.Random;

public class Utils {
    public static int[] getRandomArray(int size) {
        int[] array = new int[size];
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(15);
        }
        return array;
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
