package strategy;

import config.ClientConfig;
import connection.ConnectionManager;
import dto.MatrixData;
import exception.ClientOperationException;
import logging.ClientLogger;
import util.MatrixUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.logging.Logger;

public class MatrixDataReceptionStrategy implements ProcessingStrategy {
    private static final Logger logger = ClientLogger.getLogger(MatrixDataReceptionStrategy.class.getName());
    private static final int MAX_ELEMENTS_TO_LOG_1D = 10;
    private static final int MAX_ROWS_TO_LOG_2D = 5;
    private static final int MAX_COLS_TO_LOG_2D = 5;


    @Override
    public boolean execute(ConnectionManager connectionManager, ClientConfig clientConfig, MatrixData matrixData) throws ClientOperationException {
        try {
            ObjectInputStream ois = connectionManager.getObjectInputStream();
            logger.info("Aguardando dados das matrizes...");

            Object rowAObject = ois.readObject();
            logger.fine("Objeto rowA recebido do servidor (tipo: " + rowAObject.getClass().getName() + "): " +
                    MatrixUtils.arrayToString(truncateArrayForLogging(rowAObject, MAX_ELEMENTS_TO_LOG_1D)));

            double[] rowAConverted;
            if (rowAObject instanceof double[]) {
                rowAConverted = (double[]) rowAObject;
                logger.fine("Linha A recebida como double[].");
            } else if (rowAObject instanceof long[]) {
                long[] rowALong = (long[]) rowAObject;
                rowAConverted = new double[rowALong.length];
                for (int i = 0; i < rowALong.length; i++) {
                    rowAConverted[i] = (double) rowALong[i];
                }
                logger.fine("Linha A recebida como long[] e convertida para double[].");
            } else {
                throw new ClientOperationException("Tipo de dado inesperado para a linha da matriz A. Esperado: double[] ou long[]. Recebido: " + rowAObject.getClass().getName());
            }
            matrixData.setRowA(rowAConverted);
            logger.info("Linha da matriz A recebida e processada (" + matrixData.getRowA().length + " elementos).");

            Object matrixBObject = ois.readObject();
            logger.fine("Objeto matrixB recebido do servidor (tipo: " + matrixBObject.getClass().getName() + "): \n" +
                    MatrixUtils.sample2DArrayToString(matrixBObject, MAX_ROWS_TO_LOG_2D, MAX_COLS_TO_LOG_2D));

            double[][] matrixBConverted;
            if (matrixBObject instanceof double[][]) {
                matrixBConverted = (double[][]) matrixBObject;
                logger.fine("Matriz B recebida como double[][].");
            } else if (matrixBObject instanceof long[][]) {
                long[][] matrixBLong = (long[][]) matrixBObject;
                if (matrixBLong.length == 0) {
                    matrixBConverted = new double[0][0];
                } else {
                    int numCols = (matrixBLong.length > 0 && matrixBLong[0] != null) ? matrixBLong[0].length : 0;
                    matrixBConverted = new double[matrixBLong.length][numCols];
                    for (int i = 0; i < matrixBLong.length; i++) {
                        if (matrixBLong[i] != null) {
                            for (int j = 0; j < matrixBLong[i].length; j++) {
                                matrixBConverted[i][j] = (double) matrixBLong[i][j];
                            }
                        } else {
                            if (numCols > 0) {
                                matrixBConverted[i] = new double[numCols];
                                logger.warning("Linha " + i + " da Matriz B recebida como nula. Convertida para linha de zeros/vazia.");
                            } else {
                                matrixBConverted[i] = new double[0];
                                logger.warning("Linha " + i + " da Matriz B recebida como nula e sem colunas definidas. Convertida para linha vazia.");
                            }
                        }
                    }
                }
                logger.fine("Matriz B recebida como long[][] e convertida para double[][].");
            } else {
                throw new ClientOperationException("Tipo de dado inesperado para a matriz B. Esperado: double[][] ou long[][]. Recebido: " + matrixBObject.getClass().getName());
            }
            matrixData.setMatrixB(matrixBConverted);
            logger.info(String.format("Matriz B recebida e processada (dimensões: %dx%d).",
                    matrixData.getMatrixB().length,
                    (matrixData.getMatrixB().length > 0 && matrixData.getMatrixB()[0] != null ? matrixData.getMatrixB()[0].length : 0)));
            return true;
        } catch (IOException | ClassNotFoundException e) {
            String errorMsg = "Erro ao receber dados das matrizes: " + e.getMessage();
            logger.severe(errorMsg);
            throw new ClientOperationException(errorMsg, e);
        }
    }

    private Object truncateArrayForLogging(Object array, int maxLength) {
        if (array == null) return null;
        if (array instanceof double[]) {
            double[] dArray = (double[]) array;
            if (dArray.length > maxLength) return Arrays.copyOf(dArray, maxLength);
        } else if (array instanceof long[]) {
            long[] lArray = (long[]) array;
            if (lArray.length > maxLength) return Arrays.copyOf(lArray, maxLength);
        }

        return array;
    }
}