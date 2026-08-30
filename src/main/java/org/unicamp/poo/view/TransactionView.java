package org.unicamp.poo.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.enums.OperationType;
import static org.unicamp.poo.util.ConsoleColors.GREEN;
import static org.unicamp.poo.util.ConsoleColors.RED;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

// View responsável por lidar com interações referentes a transações.
public class TransactionView {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final MessageProvider messages;

    public TransactionView(MessageProvider messages) {
        this.messages = messages;
    }

    public Transaction readTransactionData(OperationType operationType) {
        int id = ConsoleScanner.readInt(messages.get("wallet.view.prompt.id"), messages.get("generic.confirmInvalid"));

        double quantity = ConsoleScanner.readDouble(messages.get("transaction.view.prompt.quantity"), messages.get("generic.confirmInvalid"));

        while (quantity <= 0){
            if(quantity == 0) {
                return null;
            }
            quantity = ConsoleScanner.readDouble(messages.get("transaction.view.prompt.positiveQuantity"), messages.get("generic.confirmInvalid"));
        }

        LocalDate operationDate = readOperationDate();
        return new Transaction(id, operationDate, operationType, quantity);
    }

    public void showErrorMessage(String message) {
        System.out.println(RED + message + RESET);
    }

    public void showSuccessMessage(String message) {
        System.out.println(GREEN + message + RESET);
    }

    public void displayDailyQuote(Oracle dailyQuote) {
        System.out.println("\n" + messages.get("report.oracle.display.title"));
        System.out.println(messages.get("report.oracle.display.date") + "  " + DATE_FORMAT.format(dailyQuote.getDate()));
        System.out.printf(messages.get("report.oracle.display.price") + " %.2f%n", dailyQuote.getPrice());
        System.out.println(messages.get("report.oracle.display.separator"));
    }

    public int readWalletId() {
        return ConsoleScanner.readInt(messages.get("wallet.view.prompt.id"), messages.get("generic.confirmInvalid"));
    }

    public void displayWalletBalance(double balance) {
        System.out.printf(messages.get("transaction.view.walletBalance") + " %.4f%n", balance);
    }

    public Double readQuantity() {
        double quantity = ConsoleScanner.readDouble(messages.get("transaction.view.prompt.quantity"), messages.get("generic.confirmInvalid"));
        while (quantity <= 0) {
            if (quantity == 0) {
                return null;
            }
            quantity = ConsoleScanner.readDouble(messages.get("transaction.view.prompt.positiveQuantity"), messages.get("generic.confirmInvalid"));
        }
        return quantity;
    }

    public boolean confirmRejectTransaction(String confirmMessage) {
        while (true) {
            System.out.println("\n" + confirmMessage);
            System.out.println("1 - " + messages.get("generic.confirmYes"));
            System.out.println("2 - " + messages.get("generic.confirmNo"));

            Integer option = ConsoleScanner.readIntOrNull();
            if (option == null) {
                System.out.println(messages.get("generic.confirmInvalid"));
                continue;
            }

            switch (option) {
                case 1 -> {
                    return false;
                }
                case 2 -> {
                    return true;
                }
                default -> System.out.println(messages.get("generic.confirmInvalid"));
            }
        }
    }

    public LocalDate readOperationDate() {
        return ConsoleScanner.readLocalDateOrNow(
            messages.get("transaction.view.prompt.date"),
            messages.get("generic.confirmInvalid")
        );
    }
}