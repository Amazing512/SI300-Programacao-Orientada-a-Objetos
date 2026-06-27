package org.unicamp.poo.util;

import java.util.Scanner;

public class ConsoleScanner {
    private static final Scanner scanner = new Scanner(System.in);

    private ConsoleScanner()
    {
        // empty private constructor avoids instantiation
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
}
