package strategy;

import config.ClientConfig;
import connection.ConnectionManager;
import dto.MatrixData;
import exception.ClientOperationException;
import logging.ClientLogger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

public class InitialValidationStrategy implements ProcessingStrategy {
    private static final Logger logger = ClientLogger.getLogger(InitialValidationStrategy.class.getName());
    private static final String SUCCESS_KEYWORD = "ENCONTRADA";

    @Override
    public boolean execute(ConnectionManager connectionManager, ClientConfig clientConfig, MatrixData matrixData) throws ClientOperationException {
        try {
            ObjectOutputStream oos = connectionManager.getObjectOutputStream();
            ObjectInputStream ois = connectionManager.getObjectInputStream();

            String studentId = clientConfig.getStudentId();
            logger.fine("Enviando matrícula para o servidor: " + studentId);
            oos.writeObject(studentId);
            oos.flush();
            logger.info("Matrícula '" + studentId + "' enviada.");

            Object serverResponse = ois.readObject();
            logger.fine("Resposta de validação recebida do servidor (tipo: " + serverResponse.getClass().getName() + "): " + serverResponse.toString());


            if (!(serverResponse instanceof String)) {
                String errorMsg = "Resposta de validação inesperada do servidor. Esperado: String. Recebido: " +
                        serverResponse.getClass().getName();
                logger.severe(errorMsg);
                throw new ClientOperationException(errorMsg);
            }

            String statusMessage = (String) serverResponse;
            logger.info("Mensagem do servidor (validação): " + statusMessage);

            if (statusMessage.toUpperCase().contains(SUCCESS_KEYWORD)) {
                logger.info("Matrícula validada pelo servidor (baseado na mensagem). Próxima etapa: receber dados da matriz.");
                return true;
            } else {
                logger.warning("Matrícula '" + studentId +
                        "' não validada ou não encontrada, conforme mensagem do servidor: \"" + statusMessage + "\"");
                return false;
            }

        } catch (IOException | ClassNotFoundException e) {
            String errorMsg = "Erro durante a etapa de validação inicial: " + e.getMessage();
            logger.severe(errorMsg);
            throw new ClientOperationException(errorMsg, e);
        }
    }
}
