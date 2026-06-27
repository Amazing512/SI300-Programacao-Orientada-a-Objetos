package org.unicamp.poo.view;

import java.util.Scanner;

import org.unicamp.poo.model.Wallet;
import static org.unicamp.poo.util.ConsoleColors.GREEN;
import static org.unicamp.poo.util.ConsoleColors.RED;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

/*
View class responsible for handling terminal input and output operations
regarding the Wallet module.
 */

public class WalletView {
    private final MessageProvider messages;

    // Constructor providing Dependency Injection for internationalized messages
    public WalletView(MessageProvider messages) {
        this.messages = messages;
    }

    //Reads all fields required to instantiate and register a new Wallet
    public Wallet readWalletData() {
        // Uses the centralized shared Scanner instance from the project utilities
        Scanner read = ConsoleScanner.getInstance();

        int id = ConsoleScanner.readInt(messages.get("wallet.view.prompt.id"), messages.get("generic.confirmInvalid"));

        System.out.print(messages.get("wallet.view.prompt.holder"));
        String holder = read.nextLine();

        System.out.print(messages.get("wallet.view.prompt.broker"));
        String broker = read.nextLine();

        return new Wallet(id, holder, broker);
    }

    // Prints an error message highlighted in RED color
    public void showErrorMessage(String s) {
        System.out.println(RED + s + RESET);
    }

    // Prints a success message highlighted in GREEN color
    public void showSuccessMessage(String s) {
        System.out.println(GREEN + s + RESET);
    }

    //Prompts the user for a single Wallet ID
    public int readWalletId() {
        return ConsoleScanner.readInt(messages.get("wallet.view.prompt.id"), messages.get("generic.confirmInvalid"));
    }

    // Displays the current details of a given Wallet instance formatted on the console screen
    public void displayWallet(Wallet wallet) {

        System.out.println(messages.get("wallet.view.header"));
        System.out.println(messages.get("wallet.view.id") + " " + wallet.getId());
        System.out.println(messages.get("wallet.view.holder") + " " + wallet.getHolder());
        System.out.println(messages.get("wallet.view.broker") + " " + wallet.getBroker());
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
        // Loops until a valid decision option (1 or 2) is supplied by the user
        while (true) {
            System.out.println(messages.get("wallet.view.prompt.confirmDelete"));
            System.out.println("1 - " + messages.get("generic.confirmYes"));
            System.out.println("2 - " + messages.get("generic.confirmNo"));

            Integer option = ConsoleScanner.readIntOrNull();
            if (option == null) {
                System.out.println(messages.get("generic.confirmInvalid"));
                continue;
            }

            switch (option) {
                case 1 -> {
                    return true;
                }
                case 2 -> {
                    return false;
                }
                default -> System.out.println(messages.get("generic.confirmInvalid"));
            }
        }
    }
}