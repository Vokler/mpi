import java.util.Random;

public class Utils {

    public static int[][] createFixedGraph() {
        return new int[][]{
                {0, 1, 1, 0, 1, 0, 0, 0},
                {1, 0, 0, 1, 0, 1, 0, 0},
                {1, 0, 0, 1, 0, 0, 1, 0},
                {0, 1, 1, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 1, 1, 0},
                {0, 1, 0, 0, 1, 0, 0, 1},
                {0, 0, 1, 0, 1, 0, 0, 1},
                {0, 0, 0, 1, 0, 1, 1, 0}
        };
//        return new int[][]{
//                {0, 1, 1, 0, 1, 0},
//                {1, 0, 1, 1, 0, 0},
//                {1, 1, 0, 0, 0, 1},
//                {0, 1, 0, 0, 1, 1},
//                {1, 0, 0, 1, 0, 1},
//                {0, 0, 1, 1, 1, 0}
//        };
//        return new int[][]{
//                {0, 1, 1, 0},
//                {1, 0, 0, 1},
//                {1, 0, 0, 1},
//                {0, 1, 1, 0}
//        };
    }

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

    public static int countRegularGraphEdges(int[][] graph) {
        int countVertexes = graph.length;
        int degree = getGraphDegree(graph);
        return countVertexes * degree / 2;
    }

    public static int getInitVertexCountEdges(int[][] graph) {
        /* Возвращает кол-во ребер для инициализирующей (первой) вершины. */
        int count = 0;
        int[] initVertex = graph[0];
        for (int el : initVertex) {
            if (el == 1) {
                count++;
            }
        }
        return count;
    }

    public static int getGraphDegree(int[][] graph) {
        /* Возвращает степень для Регулярого графа. */
        return getInitVertexCountEdges(graph);
    }

    private static int getVertexCountEdges(int[] vertex) {
        /* Возвращает кол-во ребер для переданной вершины графа. */
        int count = 0;
        for (int v : vertex) {
            if (v == 1) {
                count++;
            }
        }
        return count;
    }

    public static boolean isRegularGraph(int[][] graph) {
        boolean result = true;
        int countInitEdges = getInitVertexCountEdges(graph);
        for (int i = 1; i < graph.length; i++) {
            if (getVertexCountEdges(graph[i]) != countInitEdges) {
                result = false;
                break;
            }
        }
        return result;
    }

    public static boolean isHypercubeGraph(int[][] graph) {
        boolean result = true;
        if (isRegularGraph(graph)) {
            int n = getInitVertexCountEdges(graph);
            int countVertexes = graph.length;
            int countEdges = countRegularGraphEdges(graph);

            // Условия для существования Гиперкуба
            double firstCond = Math.pow(2, n);
            double secondCond = Math.pow(2, n - 1) * n;

            if ((countVertexes != firstCond) || (countEdges != secondCond)) {
                result = false;
            }
        }
        return result;
    }
}
