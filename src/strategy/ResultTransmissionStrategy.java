package strategy;

import config.ClientConfig;
import connection.ConnectionManager;
import dto.MatrixData;
import exception.ClientOperationException;
import logging.ClientLogger;
import util.MatrixUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.logging.Logger;

public class ResultTransmissionStrategy implements ProcessingStrategy {
    private static final Logger logger = ClientLogger.getLogger(ResultTransmissionStrategy.class.getName());
    private static final int MAX_ELEMENTS_TO_LOG_1D = 10;

    @Override
    public boolean execute(ConnectionManager connectionManager, ClientConfig clientConfig, MatrixData matrixData) throws ClientOperationException {
        if (matrixData.getResultRow() == null) {
            throw new ClientOperationException("Resultado da multiplicação não está disponível para envio.");
        }
        try {
            ObjectOutputStream oos = connectionManager.getObjectOutputStream();
            ObjectInputStream ois = connectionManager.getObjectInputStream();

            double[] resultToSend = matrixData.getResultRow();
            logger.fine("Enviando linha resultante para o servidor (amostra: " +
                    MatrixUtils.arrayToString(truncateArrayForLogging(resultToSend, MAX_ELEMENTS_TO_LOG_1D)) +
                    ", total elementos: " + resultToSend.length + ")");

            oos.writeObject(resultToSend);
            oos.flush();
            logger.info("Linha resultante enviada para o servidor.");

            Object finalConfirmation = ois.readObject();
            logger.info("Resposta final do servidor: " + finalConfirmation);
            logger.fine("Detalhe da resposta final do servidor (tipo: " + finalConfirmation.getClass().getName() + "): " + finalConfirmation.toString());
            return true;
        } catch (IOException | ClassNotFoundException e) {
            String errorMsg = "Erro ao transmitir resultado ou receber confirmação final: " + e.getMessage();
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