package strategy;

import config.ClientConfig;
import connection.ConnectionManager;
import dto.MatrixData;
import exception.ClientOperationException;

public interface ProcessingStrategy {
    boolean execute(ConnectionManager connectionManager, ClientConfig clientConfig, MatrixData matrixData) throws ClientOperationException;
}