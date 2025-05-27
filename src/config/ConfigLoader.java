package config;

import util.InputUtils;

public class ConfigLoader {
    private static final String DEFAULT_SERVER_IP = "10.130.34.177";
    private static final int DEFAULT_SERVER_PORT = 12345;
    private static final String DEFAULT_STUDENT_ID = "UC23103118";

    public static ClientConfig loadConfigInteractively() {
        String serverIp = InputUtils.readString("Digite o IP do Servidor (padrão: " + DEFAULT_SERVER_IP + "): ");

        if (serverIp.isEmpty()) {
            serverIp = DEFAULT_SERVER_IP;
        }

        int serverPort = InputUtils.readInt("Digite a Porta do Servidor", DEFAULT_SERVER_PORT);
        String studentId = InputUtils.readString("Digite sua Matrícula (ex: UC12345): ");

        if (studentId.isEmpty()) {
            System.err.println("Matrícula não pode ser vazia. Usando 'INVALID_ID'.");
            studentId = DEFAULT_STUDENT_ID;
        }

        return new ClientConfig(serverIp, serverPort, studentId);
    }
}
