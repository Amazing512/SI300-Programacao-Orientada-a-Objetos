package org.unicamp.poo.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConsoleScanner {
    private static final Scanner scanner = new Scanner(System.in);

    private ConsoleScanner()
    {
        // Construtor privado vazio para evitar instanciação
    }

    public static Scanner getInstance()
    {
        return (scanner);
    }

    public static int readInt(String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            Integer value = readIntOrNull();
            if (value != null) {
                return value;
            }
            System.out.println(errorMessage);
        }
    }

    public static Integer readIntOrNull() {
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }

        try {
            return Integer.valueOf(input);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public static double readDouble(String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException exception) {
                System.out.println(errorMessage);
            }
        }
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void pressEnterToContinue(String message) {
        System.out.println(message);
        scanner.nextLine();
    }

    public static String readRequiredString(String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println(ConsoleColors.RED + errorMessage + ConsoleColors.RESET);
        }
    }

    public static LocalDate readLocalDate(String prompt, String errorMessage) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException exception) {
                System.out.println(ConsoleColors.RED + errorMessage + ConsoleColors.RESET);
            }
        }
    }

    public static LocalDate readLocalDateOrNow(String prompt, String errorMessage) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException exception) {
                System.out.println(ConsoleColors.RED + errorMessage + ConsoleColors.RESET);
            }
        }
    }
}
