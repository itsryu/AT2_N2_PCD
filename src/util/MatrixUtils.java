package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class MatrixUtils {
    public static double[] multiply(double[] rowA, double[][] matrixB) {
        if (rowA == null || matrixB == null || rowA.length == 0 || matrixB.length == 0 || matrixB[0] == null || matrixB[0].length == 0) {
            throw new IllegalArgumentException("Matrizes de entrada não podem ser nulas ou vazias, e a primeira linha da matriz B não pode ser nula.");
        }

        int kSize = rowA.length;
        int numRowsB = matrixB.length;
        int numColsB = matrixB[0].length;

        if (kSize != numRowsB) {
            throw new IllegalArgumentException(
                    String.format("Dimensões incompatíveis para multiplicação: linha A tem %d elementos, matriz B tem %d linhas. Devem ser iguais.",
                            kSize, numRowsB)
            );
        }

        double[] result = new double[numColsB];
        for (int j = 0; j < numColsB; j++) {
            double sum = 0;
            for (int k = 0; k < kSize; k++) {
                sum += rowA[k] * matrixB[k][j];
            }
            result[j] = sum;
        }
        return result;
    }

    public static String arrayToString(Object array) {
        if (array == null) return "null";
        if (array instanceof double[]) return Arrays.toString((double[]) array);
        if (array instanceof long[]) return Arrays.toString((long[]) array);
        if (array instanceof int[]) return Arrays.toString((int[]) array);
        if (array instanceof Object[]) return Arrays.deepToString((Object[]) array); // Para double[][] ou long[][]
        return array.toString();
    }

    public static String sample2DArrayToString(Object matrixObject, int maxRows, int maxCols) {
        if (matrixObject == null) return "null";

        if (matrixObject instanceof double[][]) {
            double[][] matrix = (double[][]) matrixObject;
            return sample2DPrimitiveArrayToString(matrix, maxRows, maxCols, (m, r, c) -> String.valueOf(((double[][])m)[r][c]));
        } else if (matrixObject instanceof long[][]) {
            long[][] matrix = (long[][]) matrixObject;
            return sample2DPrimitiveArrayToString(matrix, maxRows, maxCols, (m, r, c) -> String.valueOf(((long[][])m)[r][c]));
        }
        return "Tipo de matriz não suportado para amostragem: " + matrixObject.getClass().getName();
    }

    @FunctionalInterface
    private interface ElementAccessor<T> {
        String get(T matrix, int r, int c);
    }

    private static <T> String sample2DPrimitiveArrayToString(T matrix, int maxRows, int maxCols, ElementAccessor<T> accessor) {
        int numRows = 0;
        int numColsInFirstRow = 0;

        if (matrix instanceof double[][]) {
            numRows = ((double[][])matrix).length;
            if (numRows > 0 && ((double[][])matrix)[0] != null) numColsInFirstRow = ((double[][])matrix)[0].length;
        } else if (matrix instanceof long[][]) {
            numRows = ((long[][])matrix).length;
            if (numRows > 0 && ((long[][])matrix)[0] != null) numColsInFirstRow = ((long[][])matrix)[0].length;
        }


        if (numRows == 0) return "[Matriz vazia]";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[Amostra da Matriz (Dimensões: %d x ~%d), Exibindo até %d linhas e %d colunas da primeira linha]:\n", numRows, numColsInFirstRow, maxRows, maxCols));
        for (int i = 0; i < Math.min(numRows, maxRows); i++) {
            sb.append("  [");

            int currentRowCols = 0;
            if (matrix instanceof double[][] && ((double[][])matrix)[i] != null) {
                currentRowCols = ((double[][])matrix)[i].length;
            } else if (matrix instanceof long[][] && ((long[][])matrix)[i] != null) {
                currentRowCols = ((long[][])matrix)[i].length;
            }

            for (int j = 0; j < Math.min(currentRowCols, maxCols); j++) {
                sb.append(accessor.get(matrix, i, j));
                if (j < Math.min(currentRowCols, maxCols) - 1) sb.append(", ");
            }
            if (currentRowCols > maxCols) sb.append("...");
            sb.append("]\n");
        }
        if (numRows > maxRows) sb.append("  ...\n");
        return sb.toString();
    }

    public static void writeToFile(String filePath, String content) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(content);
            bufferedWriter.newLine();
        }
    }
}