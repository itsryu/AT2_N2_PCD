import config.ClientConfig;
import config.ConfigLoader;
import logging.ClientLogger;
import processing.ClientProcessor;
import util.InputUtils;

import java.util.logging.Logger;

public class Main {
    private static final Logger logger = ClientLogger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown hook: Fechando handlers de log...");
            ClientLogger.closeHandlers();
        }));

        logger.info("Iniciando Cliente de Multiplicação de Matrizes...");

        ClientConfig config = ConfigLoader.loadConfigInteractively();

        logger.info(String.format("Configuração carregada: Servidor %s:%d, Matrícula: %s", config.getServerIp(), config.getServerPort(), config.getStudentId()));

        ClientProcessor processor = new ClientProcessor(config);
        processor.run();

        InputUtils.closeScanner();

        logger.info("Aplicação cliente finalizada.");
    }
}
