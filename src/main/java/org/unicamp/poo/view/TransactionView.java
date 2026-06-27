package org.unicamp.poo.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.enums.OperationType;
import static org.unicamp.poo.util.ConsoleColors.GREEN;
import static org.unicamp.poo.util.ConsoleColors.RED;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

// View class responsible for handling console interactions regarding transactions.

public class TransactionView {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private final MessageProvider messages;

    // Constructor providing Dependency Injection for internationalized messages
    public TransactionView(MessageProvider messages) {
        this.messages = messages;
    }

    public Transaction readTransactionData(OperationType operationType) {
        int id = ConsoleScanner.readInt(messages.get("wallet.view.prompt.id"), messages.get("generic.confirmInvalid"));

        double quantity = ConsoleScanner.readDouble(messages.get("transaction.view.prompt.quantity"), messages.get("generic.confirmInvalid"));

        // Forces user to enter a positive non-zero value
        while (quantity <= 0){
            if(quantity == 0) {
                // Void the operation
                return null;
            }
            quantity = ConsoleScanner.readDouble(messages.get("transaction.view.prompt.positiveQuantity"), messages.get("generic.confirmInvalid"));
        }
        return new Transaction(id, new Date(), operationType, quantity);
    }


    // Prints an error message highlighted in red color
    public void showErrorMessage(String s) {
        System.out.println(RED + s + RESET);
    }

    // Prints a success message highlighted in green color
    public void showSuccessMessage(String s) {
        System.out.println(GREEN + s + RESET);
    }

    // Shows the current day's quote consulted from the Oracle before a buy or sell
    // operation, so the user can make an informed decision.
    public void displayDailyQuote(Oracle dailyQuote) {
        System.out.println("\n----- Cotação do Dia -----");
        System.out.println("Data:  " + DATE_FORMAT.format(dailyQuote.getDate()));
        System.out.printf("Preço: %.2f%n", dailyQuote.getPrice());
        System.out.println("---------------------------");
    }

    public boolean confirmTransaction(String confirmMessage) {
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