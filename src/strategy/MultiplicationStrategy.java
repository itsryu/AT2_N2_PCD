package strategy;

import config.ClientConfig;
import connection.ConnectionManager;
import dto.MatrixData;
import exception.ClientOperationException;
import logging.ClientLogger;
import util.MatrixUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

public class MultiplicationStrategy implements ProcessingStrategy {
    private static final Logger logger = ClientLogger.getLogger(MultiplicationStrategy.class.getName());

    @Override
    public boolean execute(ConnectionManager connectionManager, ClientConfig clientConfig, MatrixData matrixData) throws ClientOperationException {
        if (matrixData.getRowA() == null || matrixData.getMatrixB() == null) {
            throw new ClientOperationException("Dados das matrizes não foram carregados para multiplicação.");
        }
        try {
            logger.info("Iniciando multiplicação da linha A pela matriz B...");
            logger.fine("Linha A para multiplicação (amostra): " + MatrixUtils.arrayToString(truncateArrayForLogging(matrixData.getRowA(), 10)));
            logger.fine("Matriz B para multiplicação (amostra): \n" + MatrixUtils.sample2DArrayToString(matrixData.getMatrixB(), 5, 5));

            double[] result = MatrixUtils.multiply(matrixData.getRowA(), matrixData.getMatrixB());
            matrixData.setResultRow(result);
            logger.info("Multiplicação concluída. Linha resultante (" + result.length + " elementos).");
            logger.fine("Linha Resultante (amostra): " + MatrixUtils.arrayToString(truncateArrayForLogging(result, 10)));
            MatrixUtils.writeToFile( "multiplication_result.txt", MatrixUtils.arrayToString(truncateArrayForLogging(result, 1000)));
            return true;
        } catch (IllegalArgumentException | IOException e) {
            String errorMsg = "Erro durante a multiplicação: " + e.getMessage();
            logger.severe(errorMsg);
            throw new ClientOperationException(errorMsg, e);
        }
    }

    private Object truncateArrayForLogging(Object array, int maxLength) {
        if (array == null) return null;
        if (array instanceof double[]) {
            double[] dArray = (double[]) array;
            if (dArray.length > maxLength) return Arrays.copyOf(dArray, maxLength);
        }

        return array;
    }
}
