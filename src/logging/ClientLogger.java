package logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.*;

public class ClientLogger {
    private static final String LOG_FILE_NAME = "matrix_client.log";
    private static FileHandler fileHandler = null;

    static {
        Logger appLogger = Logger.getLogger("");
        appLogger.setLevel(Level.FINE);

        for (Handler handler : appLogger.getHandlers()) {
            appLogger.removeHandler(handler);
        }

        InputStream stream = ClientLogger.class.getClassLoader().
                getResourceAsStream("logging.properties");
        if (stream != null) {
            try {
                LogManager.getLogManager().readConfiguration(stream);
                Logger.getLogger(ClientLogger.class.getName()).info("Configuração de logging carregada de logging.properties.");
            } catch (IOException e) {
                System.err.println("Não foi possível carregar a configuração de logging de logging.properties: " + e.getMessage());
            }
        } else {
            System.err.println("Arquivo logging.properties não encontrado. Usando configuração de logging programática para 'com.example.matrixclient'.");
        }

        try {
            fileHandler = new FileHandler(LOG_FILE_NAME, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.FINE);
            appLogger.addHandler(fileHandler);

            boolean consoleHandlerExistsForAppLogger = false;
            for (java.util.logging.Handler handler : appLogger.getHandlers()) {
                if (handler instanceof ConsoleHandler) {
                    consoleHandlerExistsForAppLogger = true;
                    break;
                }
            }
            if (!consoleHandlerExistsForAppLogger) {
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setLevel(Level.INFO); // Log INFO e acima para o console
                consoleHandler.setFormatter(new SimpleFormatter());
                appLogger.addHandler(consoleHandler);
            }

            appLogger.setUseParentHandlers(false);
            Logger.getLogger(ClientLogger.class.getName()).info("FileHandler configurado para " + LOG_FILE_NAME + " (append: true) com nível FINE. ConsoleHandler para 'com.example.matrixclient' configurado com nível INFO.");

        } catch (IOException e) {
            System.err.println("Erro ao configurar FileHandler para logging: " + e.getMessage());
            e.printStackTrace();
        } catch (SecurityException e) {
            System.err.println("Erro de segurança ao configurar FileHandler (verifique permissões de escrita): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Logger getLogger(String className) {
        if (className.startsWith("")) {
            return Logger.getLogger(className);
        }

        System.err.println("Aviso: Obtendo logger para classe fora do pacote esperado: " + className);
        return Logger.getLogger(className);
    }

    public static void closeHandlers() {
        Logger logger = Logger.getLogger(ClientLogger.class.getName());
        logger.info("Fechando handlers de log (shutdown hook)...");
        if (fileHandler != null) {
            try {
                fileHandler.flush();
                fileHandler.close();
                logger.info("FileHandler fechado com sucesso.");
            } catch (SecurityException e) {
                logger.log(Level.SEVERE, "Erro de segurança ao fechar FileHandler.", e);
            }
        } else {
            logger.warning("FileHandler era nulo, não pode ser fechado.");
        }
    }
}
