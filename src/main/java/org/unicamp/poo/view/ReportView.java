package org.unicamp.poo.view;

import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.util.ConsoleScanner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/*  View class responsible for interacting with the user and formatting console
    layouts for financial and ranking reports. */

public class ReportView {

    // ANSI Escape codes for coloring console output texts
    public static final String reset = "\u001B[0m";
    public static final String green = "\u001B[32m";
    public static final String red = "\u001B[31m";

    // Date format used for Oracle input and output
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public int readWalletIdReport(){
        Scanner read = ConsoleScanner.getInstance();

        System.out.print("Digite o ID da carteira para gerar o relatório financeiro: ");
        int id = read.nextInt();
        read.nextLine();

        return id;
    }

    // Renders a financial panel block showing metrics and net values.

    public void showFinancialReport(int walletId, double totalCashIn, double totalCashOut, double result) {

        System.out.println("\n----------------------------------");
        System.out.println("       RELATÓRIO FINANCEIRO       ");
        System.out.println("----------------------------------");
        System.out.println("ID da carteira: " + walletId);
        System.out.printf("Total de entrada de moedas: %.2f%n", totalCashIn);
        System.out.printf("Total de saídas de moedas: %.2f%n", totalCashOut);
        System.out.println("----------------------------------");
        System.out.print("Resultado Líquido: ");

        if (result < 0){
            System.out.println(red + String.format("%.2f", result) + reset);
        }
        else {
            System.out.println(green + String.format("%.2f", result) + reset);
        }
        System.out.println("--------------------------------");

    }

    // Formats and prints a multi-column aligned dashboard showcasing the wallets ranking.

    public void showSortedWalletsReport(List<Wallet> wallets, double[] sortedBalances) {
        System.out.println("\n---------------------------------");
        System.out.println("       CARTEIRAS POR SALDO        ");
        System.out.println("---------------------------------");

        System.out.printf("%-10s | %-25s | %-15s%n", "ID", "PROPRIETÁRIO", "SALDO ATUAL");
        System.out.println("---------------------------------");

        // Iterates and flushes formatted rows to the console
        for(int i = 0; i < wallets.size(); i++){
            Wallet w = wallets.get(i);
            double balance = sortedBalances[i];

            System.out.printf("%-10d | %-25s | %.2f%n", w.getId(), w.getHolder(), balance);
        }
        System.out.println("---------------------------------");
    }

    // Prints an error message highlighted in red color
    public void showErrorMessage(String s) {
        System.out.println(red + s + reset);
    }

    // Prints a success message highlighted in green color
    public void showSuccessMessage(String s) {
        System.out.println(green + s + reset);
    }

    // Prompts the user for a date, used for Oracle searches, edits and deletions.
    public Date readOracleDate() {
        Scanner read = ConsoleScanner.getInstance();

        // Loops until the user enters a valid date in the expected format
        while (true) {
            System.out.print("Digite a data da cotação (dd/MM/yyyy): ");
            String input = read.nextLine();

            try {
                DATE_FORMAT.setLenient(false); // Rejects invalid dates like 32/13/2024
                return DATE_FORMAT.parse(input);
            } catch (ParseException e) {
                System.out.println(red + "Data inválida. Use o formato dd/MM/yyyy." + reset);
            }
        }
    }

    // Reads all fields required to register a new Oracle quote.
    public Oracle readOracleData() {
        Scanner read = ConsoleScanner.getInstance();

        Date date = readOracleDate();

        System.out.print("Digite o preço da moeda: ");
        double price = read.nextDouble();
        read.nextLine();

        while (price <= 0) {
            System.out.print("O preço deve ser positivo. Digite novamente: ");
            price = read.nextDouble();
            read.nextLine();
        }

        return new Oracle(date, price);
    }

    // Displays the details of a single Oracle quote on the console.
    public void displayOracle(Oracle oracle) {
        System.out.println("\n----- Cotação do Dia -----");
        System.out.println("Data:  " + DATE_FORMAT.format(oracle.getDate()));
        System.out.printf("Preço: %.2f%n", oracle.getPrice());
        System.out.println("--------------------------");
    }

    // Iterates through a list of Oracle quotes and prints them formatted to the console.
    public void displayOracleList(List<Oracle> oracles) {
        System.out.println("\n----- Lista de Cotações -----");
        for (int i = 0; i < oracles.size(); i++) {
            Oracle o = oracles.get(i);
            System.out.println("----------------------------------------------");
            System.out.println("Cotação " + (i + 1));
            System.out.println("Data:  " + DATE_FORMAT.format(o.getDate()));
            System.out.printf("Preço: %.2f%n", o.getPrice());
        }
        System.out.println("----------------------------------------------");
    }

    // Prompts the user for an updated price, maintaining the original date.
    public Oracle readOracleUpdates(Oracle editableOracle) {
        Scanner read = ConsoleScanner.getInstance();

        System.out.println("Data atual: " + DATE_FORMAT.format(editableOracle.getDate()));
        System.out.printf("Preço atual: %.2f%n", editableOracle.getPrice());

        System.out.print("Digite o novo preço da moeda: ");
        double price = read.nextDouble();
        read.nextLine();

        while (price <= 0) {
            System.out.print("O preço deve ser positivo. Digite novamente: ");
            price = read.nextDouble();
            read.nextLine();
        }

        // Keeps the same date — the key of an Oracle is its date and cannot be changed
        return new Oracle(editableOracle.getDate(), price);
    }

    // Prompts a confirmation choice to safely execute irreversible deletion actions.
    public boolean confirmDeletion(Oracle removableOracle) {
        Scanner read = ConsoleScanner.getInstance();

        while (true) {
            System.out.println("Deseja excluir a cotação do dia " + DATE_FORMAT.format(removableOracle.getDate()) + "? (Essa ação não é reversível)");
            System.out.println("1- Sim");
            System.out.println("2- Não");

            int option = read.nextInt();
            read.nextLine();

            if (option == 1) {
                return true;
            } else if (option == 2) {
                return false;
            } else {
                System.out.println("Opção inválida.");
            }
        }
    }
}