package org.unicamp.poo.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Classe utilitária para leitura, conversão e validação de dados fornecidos pelo usuário via console.
 * Centraliza o {@link Scanner} do sistema e fornece rotinas seguras para leitura de números, textos e datas.
 */
public class ConsoleScanner {
    private static final Scanner scanner = new Scanner(System.in);

    private ConsoleScanner()
    {
    }

    /**
     * Obtém a instância única de {@link Scanner} compartilhada por toda a aplicação.
     *
     * @return o {@link Scanner} configurado para leitura de {@link System#in}
     */
    public static Scanner getInstance()
    {
        return (scanner);
    }

    /**
     * Solicita e lê um número inteiro de forma obrigatória, repetindo o prompt até obter entrada válida.
     *
     * @param prompt       o texto de solicitação exibido ao usuário
     * @param errorMessage a mensagem de erro caso o valor digitado não seja um número inteiro válido
     * @return o valor inteiro lido
     */
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

    /**
     * Lê a próxima linha do console e tenta convertê-la para {@link Integer}.
     *
     * @return o valor inteiro convertido, ou null se a entrada estiver vazia ou for não numérica
     */
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

    /**
     * Solicita e lê um número em ponto flutuante (double), repetindo o prompt até a entrada ser válida.
     *
     * @param prompt       o texto de solicitação exibido ao usuário
     * @param errorMessage a mensagem de erro caso o valor não possa ser convertido para double
     * @return o valor numérico lido como {@code double}
     */
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

    /**
     * Envia códigos de escape ANSI para limpar a tela do terminal e reposicionar o cursor no topo.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Exibe uma mensagem instrutiva e pausa a execução do console até o usuário pressionar a tecla Enter.
     *
     * @param message a mensagem exibida antes da pausa
     */
    public static void pressEnterToContinue(String message) {
        System.out.println(message);
        scanner.nextLine();
    }

    /**
     * Solicita e lê uma cadeia de caracteres obrigatória, rejeitando textos vazios ou em branco.
     *
     * @param prompt       o texto de solicitação exibido ao usuário
     * @param errorMessage a mensagem de erro exibida caso a entrada esteja em branco
     * @return o texto digitado sem espaços no início e fim
     */
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

    /**
     * Solicita e lê uma data no formato brasileiro "dd/MM/yyyy", repetindo até receber um formato válido.
     *
     * @param prompt       o texto de solicitação exibido ao usuário
     * @param errorMessage a mensagem de erro caso a data não corresponda ao formato esperado
     * @return a data lida como {@link LocalDate}
     */
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

    /**
     * Solicita e lê uma data no formato "dd/MM/yyyy", assumindo a data atual caso a linha esteja em branco.
     *
     * @param prompt       o texto de solicitação exibido ao usuário
     * @param errorMessage a mensagem de erro caso a data informada seja inválida
     * @return a data lida ou a data atual ({@link LocalDate#now()}) se deixada em branco
     */
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
