package org.unicamp.poo.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

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
}