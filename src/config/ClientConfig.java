package config;

public class ClientConfig {
    private final String serverIp;
    private final int serverPort;
    private final String studentId;

    public ClientConfig(String serverIp, int serverPort, String studentId) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.studentId = studentId;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getStudentId() {
        return studentId;
    }
}