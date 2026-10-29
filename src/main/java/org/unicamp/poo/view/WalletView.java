package org.unicamp.poo.view;

import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.WalletFactory;
import static org.unicamp.poo.util.ConsoleColors.GREEN;
import static org.unicamp.poo.util.ConsoleColors.RED;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

public class WalletView {
    private final MessageProvider messages;

    public WalletView(MessageProvider messages) {
        this.messages = messages;
    }

    public Wallet readWalletData() {
        String holder = ConsoleScanner.readRequiredString(
                messages.get("wallet.view.prompt.holder"),
                messages.get("validation.required")
        );

        String broker = ConsoleScanner.readRequiredString(
                messages.get("wallet.view.prompt.broker"),
                messages.get("validation.required")
        );

        return WalletFactory.create(holder, broker);
    }

    public void showErrorMessage(String message) {
        System.out.println(RED + message + RESET);
    }

    public void showSuccessMessage(String message) {
        System.out.println(GREEN + message + RESET);
    }

    public int readWalletId() {
        return ConsoleScanner.readInt(messages.get("wallet.view.prompt.id"), messages.get("generic.confirmInvalid"));
    }

    public void displayWallet(Wallet wallet) {

        System.out.println(messages.get("wallet.view.header"));
        System.out.println(messages.get("wallet.view.id") + " " + wallet.getId());
        System.out.println(messages.get("wallet.view.holder") + " " + wallet.getHolder());
        System.out.println(messages.get("wallet.view.broker") + " " + wallet.getBroker());
    }

    public Wallet readWalletUpdates(Wallet editableWallet) {
        String holder = ConsoleScanner.readRequiredString(
                messages.get("wallet.view.prompt.newHolder"),
                messages.get("validation.required")
        );

        String broker = ConsoleScanner.readRequiredString(
                messages.get("wallet.view.prompt.newBroker"),
                messages.get("validation.required")
        );

        return WalletFactory.withUpdatedData(editableWallet.getId(), holder, broker);
    }

    public boolean confirmDeletion() {
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