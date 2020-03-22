import java.util.Random;

public class Utils {
    public static int randomInt(int min, int max) {
        int diff = max - min;
        Random random = new Random();
        int num = random.nextInt(diff + 1);
        num += min;
        return num;
    }

    public static int[][] createGraph(int size) {
        int[][] graph = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                graph[i][j] = randomInt(0, 1);
            }
        }
        return graph;
    }

    public static void printGraph(int[][] graph) {
        for (int[] el : graph) {
            for (int i : el) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    public static int countEdges(int[][] graph) {
        int count = 0;
        for (int[] el : graph) {
            for (int i : el) {
                if (i == 1) {
                    count += i;
                }
            }
        }
        return count;
    }

    public static int[] matrixInArray(int[][] graph) {
        int counter = 0;
        int size = graph.length;
        int[] array = new int[size * size];
        for (int[] el : graph) {
            for (int j = 0; j < size; j++) {
                array[counter] = el[j];
                counter++;
            }
        }
        return array;
    }

    public static int getStartIndex(int rank, int k) {
        if (rank != 0) {
            return (rank - 1) * k;
        }
        return 0;
    }

    public static int getStep(int start, int k, int N) {
        int end = start + k;
        if (N < end) {
            end = N;
        }
        return end - start;
    }
}
