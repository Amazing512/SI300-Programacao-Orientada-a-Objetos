package org.unicamp.poo.view;

import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

import java.util.Scanner;

/*
View class responsible for handling terminal input and output operations
regarding the Wallet module.
 */

public class WalletView {
    // ANSI Escape codes for coloring console output texts
    public static final String reset = "\u001B[0m";
    public static final String green = "\u001B[32m";
    public static final String red = "\u001B[31m";

    private final MessageProvider messages;

    // Constructor providing Dependency Injection for internationalized messages
    public WalletView(MessageProvider messages) {
        this.messages = messages;
    }

    //Reads all fields required to instantiate and register a new Wallet
    public Wallet readWalletData() {
        // Uses the centralized shared Scanner instance from the project utilities
        Scanner read = ConsoleScanner.getInstance();

        System.out.print(messages.get("wallet.view.prompt.id"));
        int id = read.nextInt();
        read.nextLine(); // Consumes the leftover newline character to prevent skipping inputs

        System.out.print(messages.get("wallet.view.prompt.holder"));
        String holder = read.nextLine();

        System.out.print(messages.get("wallet.view.prompt.broker"));
        String broker = read.nextLine();

        return new Wallet(id, holder, broker);
    }

    // Prints an error message highlighted in red color
    public void showErrorMessage(String s) {
        System.out.println(red + s + reset);
    }

    // Prints a success message highlighted in green color
    public void showSuccessMessage(String s) {
        System.out.println(green + s + reset);
    }

    //Prompts the user for a single Wallet ID

    public int readWalletId() {
        Scanner read = ConsoleScanner.getInstance();

        System.out.print(messages.get("wallet.view.prompt.id"));
        int id = read.nextInt();
        read.nextLine();

        return id;
    }

    // Displays the current details of a given Wallet instance formatted on the console screen
    public void displayWallet(Wallet wallet) {

        System.out.println(messages.get("wallet.view.header"));
        System.out.println(messages.get("wallet.view.id") + wallet.getId());
        System.out.println(messages.get("wallet.view.holder") + wallet.getHolder());
        System.out.println(messages.get("wallet.view.broker") + wallet.getBroker());
    }

    //Prompts the user for updated holder and broker details, maintaining the original entity ID
    public Wallet readWalletUpdates(Wallet editableWallet) {
        Scanner read = ConsoleScanner.getInstance();

        System.out.print(messages.get("wallet.view.prompt.newHolder"));
        String holder = read.nextLine();

        System.out.print(messages.get("wallet.view.prompt.newBroker"));
        String broker = read.nextLine();

        // Creates a new instance using the same ID from the target editable wallet
        Wallet wallet = new Wallet(editableWallet.getId(), holder, broker);
        return wallet;
    }

    // Prompts a confirmation choice to safely execute irreversible deletion actions
    public boolean confirmDeletion(Wallet removableWallet) {
        Scanner read = ConsoleScanner.getInstance();

        // Loops until a valid decision option (1 or 2) is supplied by the user
        while (true) {
            System.out.println(messages.get("wallet.view.prompt.confirmDelete"));
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