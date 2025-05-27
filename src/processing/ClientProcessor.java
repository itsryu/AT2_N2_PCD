package processing;

import config.ClientConfig;
import connection.ConnectionManager;
import dto.MatrixData;
import exception.ClientOperationException;
import logging.ClientLogger;
import strategy.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ClientProcessor {
    private static final Logger logger = ClientLogger.getLogger(ClientProcessor.class.getName());
    private final ClientConfig clientConfig;
    private final List<ProcessingStrategy> strategies;

    public ClientProcessor(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.strategies = Arrays.asList(
                new InitialValidationStrategy(),
                new MatrixDataReceptionStrategy(),
                new MultiplicationStrategy(),
                new ResultTransmissionStrategy()
        );
    }

    public void run() {
        try (ConnectionManager connectionManager = new ConnectionManager(clientConfig)) {
            connectionManager.connect();

            MatrixData matrixData = new MatrixData();

            for (ProcessingStrategy strategy : strategies) {
                logger.fine("Executando strategy: " + strategy.getClass().getSimpleName());
                if (!strategy.execute(connectionManager, clientConfig, matrixData)) {
                    logger.warning("Strategy " + strategy.getClass().getSimpleName() + " indicou interrupção do processo.");
                    break;
                }
            }
            logger.info("Processamento do cliente concluído.");
        } catch (ClientOperationException e) {
            logger.severe("Falha na operação do cliente: " + e.getMessage());
            if(e.getCause() != null) {
                logger.log(java.util.logging.Level.SEVERE, "Causa da falha: ", e.getCause());
            }
        } catch (IOException e) {
            logger.severe("Erro de conexão não recuperável: " + e.getMessage());
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Erro inesperado no processador do cliente: " + e.getMessage(), e);
        }
    }
}