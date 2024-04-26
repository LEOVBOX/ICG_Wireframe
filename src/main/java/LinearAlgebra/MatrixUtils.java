package LinearAlgebra;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MatrixUtils {
    public static double[][] multiply(double[][] A, double[][] B) throws Exception {
        int numRowsA = A.length;
        int numColsA = A[0].length;
        int numRowsB = B.length;
        int numColsB = B[0].length;

        if (numRowsA != numColsB || numColsA != numRowsB) {
            throw new IllegalArgumentException("Number of rows first matrix must equals number of cols of second matrix");
        }

        double[][] result = new double[numRowsA][numColsB];

        int numThreads;
        int maxDimension = Math.max(numRowsA, numRowsB);
        int maxAvailableProcessors = Runtime.getRuntime().availableProcessors();

        numThreads = Math.min(maxDimension, maxAvailableProcessors);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numRowsA; i++) {
            for (int j = 0; j < numColsB; j++) {
                final int row = i;
                final int col = j;
                futures.add(executor.submit(() -> {
                    double sum = 0.0;
                    for (int k = 0; k < numColsA; k++) {
                        sum += A[row][k] * B[k][col];
                    }
                    result[row][col] = sum;
                }));
            }
        }

        for (Future<?> future : futures) {
            // Waiting for finishing all processes
            future.get();
        }

        executor.shutdown();

        return result;
    }

    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }

    /*public static double[] multiply(double[][] matrix, double[] vector) throws ExecutionException, InterruptedException {
        int numRows = matrix.length;
        int numCols = matrix[0].length;

        if (numCols != vector.length) {
            throw new IllegalArgumentException("The number of columns in the matrix must equal the length of the vector");
        }

        double[] result = new double[numRows];

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            final int row = i;
            futures.add(executor.submit(() -> {
                double sum = 0.0;
                for (int j = 0; j < numCols; j++) {
                    sum += matrix[row][j] * vector[j];
                }
                result[row] = sum;
            }));
        }

        for (Future<?> future : futures) {
            // Waiting for finishing all processes
            future.get();
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }*/

    public static double[] multiply(double[][] matrix, double[] vector, boolean isTransposed) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[] result = new double[isTransposed ? cols : rows];

        if (isTransposed) {
            if (vector.length != rows) {
                throw new IllegalArgumentException("Vector length must be equal to the number of rows in the matrix.");
            }
            for (int j = 0; j < cols; j++) {
                double sum = 0.0;
                for (int i = 0; i < rows; i++) {
                    sum += matrix[i][j] * vector[i];
                }
                result[j] = sum;
            }
        } else {
            if (vector.length != cols) {
                throw new IllegalArgumentException("Vector length must be equal to the number of columns in the matrix.");
            }
            for (int i = 0; i < rows; i++) {
                double sum = 0.0;
                for (int j = 0; j < cols; j++) {
                    sum += matrix[i][j] * vector[j];
                }
                result[i] = sum;
            }
        }

        return result;
    }

    public static void printArray(double[] array) {
        System.out.print("[ ");
        for (double element : array) {
            System.out.print(element + " ");
        }
        System.out.println("]");
    }

    public static double[][] copy(double[][] original) {
        if (original == null) {
            return null;
        }

        int rows = original.length;
        double[][] copy = new double[rows][];

        for (int i = 0; i < rows; i++) {
            if (original[i] != null) {
                int cols = original[i].length;
                copy[i] = new double[cols];
                System.arraycopy(original[i], 0, copy[i], 0, cols);
            }
        }

        return copy;
    }

    public static double[][] multiply(double[][] matrix, double num) {
        double[][] result = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                result[i][j] = matrix[i][j] * num;
            }
        }

        return result;
    }

    public static double multiply(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must have the same length.");
        }

        double result = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            result += vector1[i] * vector2[i];
        }

        return result;
    }

}
