package org.unicamp.poo.view;

import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.enums.OperationType;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

// View class responsible for handling console interactions regarding transactions.

public class TransactionView {

    // ANSI Escape codes for coloring console output texts
    public static final String reset = "\u001B[0m";
    public static final String green = "\u001B[32m";
    public static final String red = "\u001B[31m";

    private final MessageProvider messages;

    // Constructor providing Dependency Injection for internationalized messages
    public TransactionView(MessageProvider messages) {
        this.messages = messages;
    }

    public Transaction readTransactionData(OperationType operationType) {
        Scanner read = ConsoleScanner.getInstance();

        System.out.print(messages.get("wallet.view.prompt.id"));
        int id = read.nextInt();
        read.nextLine();

        System.out.print(messages.get("transaction.view.prompt.quantity"));
        double quantity = read.nextDouble();

        // Forces user to enter a positive non-zero value
        while (quantity <= 0){
            System.out.print(messages.get("transaction.view.prompt.positiveQuantity"));
            quantity = read.nextDouble();
        }
        return new Transaction(id, new Date(), operationType, quantity);
    }

    public int readWalletForHistory(){
        Scanner read = ConsoleScanner.getInstance();

        System.out.println(messages.get("transaction.view.prompt.historyId"));
        int id = read.nextInt();
        read.nextLine();

        return id;
    }

    // Iterates through a list of transactions and prints them formatted to the console.
    public void showHistory(List<Transaction> transactions) {

    }

    // Prints an error message highlighted in red color
    public void showErrorMessage(String s) {
        System.out.println(red + s + reset);
    }

    // Prints a success message highlighted in green color
    public void showSuccessMessage(String s) {
        System.out.println(green + s + reset);
    }

    public void displayDailyQuote(Oracle dailyQuote) {
    }
}