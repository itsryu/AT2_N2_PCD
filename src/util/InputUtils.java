package util;

import logging.ClientLogger;

import java.util.Scanner;
import java.util.logging.Logger;

public class InputUtils {
    private static final Logger logger = ClientLogger.getLogger(InputUtils.class.getName());
    private static final Scanner scanner = new Scanner(System.in);

    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt, int defaultValue) {
        System.out.print(prompt + " (padrão: " + defaultValue + "): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            logger.warning("Entrada inválida para número inteiro: '" + input + "'. Usando valor padrão: " + defaultValue);
            return defaultValue;
        }
    }

    public static void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}