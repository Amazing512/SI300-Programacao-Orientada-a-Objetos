package org.unicamp.poo.view;

import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/* View class responsible for interacting with the user and formatting console
    layouts for financial and ranking reports. */

public class ReportView {

    // ANSI Escape codes for coloring console output texts
    public static final String reset = "\u001B[0m";
    public static final String green = "\u001B[32m";
    public static final String red = "\u001B[31m";

    // Date format used for Oracle input and output
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private final MessageProvider messages;

    // Constructor providing Dependency Injection for internationalized messages
    public ReportView(MessageProvider messages) {
        this.messages = messages;
    }

    public int readWalletIdReport(){
        Scanner read = ConsoleScanner.getInstance();

        System.out.print(messages.get("report.view.prompt.financialId"));
        int id = read.nextInt();
        read.nextLine();

        return id;
    }

    // Renders a financial panel block showing metrics and net values.

    public void showFinancialReport(int walletId, double totalCashIn, double totalCashOut, double result) {

        System.out.println(messages.get("report.view.financial.header"));
        System.out.println(messages.get("report.view.financial.id") + walletId);
        System.out.printf(messages.get("report.view.financial.cashIn"), totalCashIn);
        System.out.printf(messages.get("report.view.financial.cashOut"), totalCashOut);
        System.out.println("----------------------------------");
        System.out.print(messages.get("report.view.financial.resultLabel"));

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
        System.out.println(messages.get("report.view.sorted.header"));
        System.out.println(messages.get("report.view.sorted.columns"));
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
            System.out.print(messages.get("oracle.view.prompt.date"));
            String input = read.nextLine();

            try {
                DATE_FORMAT.setLenient(false); // Rejects invalid dates like 32/13/2024
                return DATE_FORMAT.parse(input);
            } catch (ParseException e) {
                System.out.println(red + messages.get("oracle.view.prompt.invalidDate") + reset);
            }
        }
    }

    // Reads all fields required to register a new Oracle quote.
    public Oracle readOracleData() {
        Scanner read = ConsoleScanner.getInstance();

        Date date = readOracleDate();

        System.out.print(messages.get("oracle.view.prompt.price"));
        double price = read.nextDouble();
        read.nextLine();

        while (price <= 0) {
            System.out.print(messages.get("oracle.view.prompt.positivePrice"));
            price = read.nextDouble();
            read.nextLine();
        }

        return new Oracle(date, price);
    }

    // Displays the details of a single Oracle quote on the console.
    public void displayOracle(Oracle oracle) {
        System.out.println(messages.get("oracle.view.single.header"));
        System.out.println(messages.get("transaction.view.history.date") + DATE_FORMAT.format(oracle.getDate()));
        System.out.printf(messages.get("oracle.view.single.price"), oracle.getPrice());
        System.out.println("--------------------------");
    }

    // Iterates through a list of Oracle quotes and prints them formatted to the console.
    public void displayOracleList(List<Oracle> oracles) {
        System.out.println(messages.get("oracle.view.list.header"));
        for (int i = 0; i < oracles.size(); i++) {
            Oracle o = oracles.get(i);
            System.out.println(messages.get("oracle.view.list.item") + (i + 1));
            System.out.println(messages.get("transaction.view.history.date") + DATE_FORMAT.format(o.getDate()));
            System.out.printf(messages.get("oracle.view.single.price"), o.getPrice());
        }
        System.out.println("----------------------------------------------");
    }

    // Prompts the user for an updated price, maintaining the original date.
    public Oracle readOracleUpdates(Oracle editableOracle) {
        Scanner read = ConsoleScanner.getInstance();

        System.out.println(messages.get("oracle.view.current.date") + DATE_FORMAT.format(editableOracle.getDate()));
        System.out.printf(messages.get("oracle.view.current.price"), editableOracle.getPrice());

        System.out.print(messages.get("oracle.view.prompt.price"));
        double price = read.nextDouble();
        read.nextLine();

        while (price <= 0) {
            System.out.print(messages.get("oracle.view.prompt.positivePrice"));
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
            System.out.printf(messages.get("oracle.view.prompt.confirmDelete") + "%n", DATE_FORMAT.format(removableOracle.getDate()));
            System.out.println(messages.get("oracle.view.confirm.yes"));
            System.out.println(messages.get("oracle.view.confirm.no"));

            int option = read.nextInt();
            read.nextLine();

            if (option == 1) {
                return true;
            } else if (option == 2) {
                return false;
            } else {
                System.out.println(messages.get("report.view.invalidOption"));
            }
        }
    }
}