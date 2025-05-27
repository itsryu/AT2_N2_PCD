package connection;

import config.ClientConfig;
import logging.ClientLogger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class ConnectionManager implements AutoCloseable {
    private static final Logger logger = ClientLogger.getLogger(ConnectionManager.class.getName());
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private final ClientConfig config;

    public ConnectionManager(ClientConfig config) {
        this.config = config;
    }

    public void connect() throws IOException {
        try {
            this.socket = new Socket(config.getServerIp(), config.getServerPort());
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());
            logger.info(String.format("Conectado ao servidor: %s:%d", config.getServerIp(), config.getServerPort()));
        } catch (UnknownHostException e) {
            String errorMsg = String.format("Servidor não encontrado (DNS ou IP inválido): %s", config.getServerIp());
            logger.severe(errorMsg + " - " + e.getMessage());
            throw new IOException(errorMsg, e);
        } catch (IOException e) {
            String errorMsg = String.format("Erro de E/S ao tentar conectar com %s:%d", config.getServerIp(), config.getServerPort());
            logger.severe(errorMsg + " - " + e.getMessage());
            throw new IOException(errorMsg, e);
        }
    }

    public ObjectOutputStream getObjectOutputStream() {
        if (oos == null) throw new IllegalStateException("Output stream não inicializado. Conexão foi estabelecida?");
        return oos;
    }

    public ObjectInputStream getObjectInputStream() {
        if (ois == null) throw new IllegalStateException("Input stream não inicializado. Conexão foi estabelecida?");
        return ois;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    @Override
    public void close() {
        logger.info("Fechando conexão com o servidor...");
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
            logger.info("Conexão fechada.");
        } catch (IOException e) {
            logger.warning("Erro ao fechar recursos de conexão: " + e.getMessage());
        }
    }
}