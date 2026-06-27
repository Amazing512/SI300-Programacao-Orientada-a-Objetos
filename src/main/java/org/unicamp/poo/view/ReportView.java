package org.unicamp.poo.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.enums.OperationType;
import static org.unicamp.poo.util.ConsoleColors.GREEN;
import static org.unicamp.poo.util.ConsoleColors.RED;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

/* View class responsible for interacting with the user and formatting console
    layouts for financial and ranking reports. */

public class ReportView {

    private final MessageProvider messages;

    // Date format used for Oracle input and output
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    // Constructor providing Dependency Injection for internationalized messages
    public ReportView(MessageProvider messages) {
        this.messages = messages;
    }

    public int readWalletIdReport(){
        return readWalletIdReport(messages.get("report.wallet.id.prompt"));
    }

    public int readWalletIdReport(String prompt){
        Scanner read = ConsoleScanner.getInstance();

        System.out.print(prompt);
        int id = read.nextInt();
        read.nextLine();

        return id;
    }

    // Renders a financial panel block showing metrics and net values.
    public void showFinancialReport(int walletId, double totalCashIn, double totalCashOut, double result) {
        System.out.println("\n----------------------------------");
        System.out.println(messages.get("report.financial.title"));
        System.out.println("----------------------------------");
        System.out.println(messages.get("report.financial.walletId") + " " +  walletId);
        System.out.printf(messages.get("report.financial.totalCashIn") + " " +  "%.2f%n", totalCashIn);
        System.out.printf(messages.get("report.financial.totalCashOut") + " " + "%.2f%n", totalCashOut);
        System.out.println("----------------------------------");
        System.out.print(messages.get("report.financial.result"));

        if (result < 0){
            System.out.println(RED + String.format("%.2f", result) + RESET);
        }
        else {
            System.out.println(GREEN + String.format("%.2f", result) + RESET);
        }
        System.out.println("--------------------------------");

    }

    // Prints wallets sorted by identifier.
    public void showWalletsOrderedByIdReport(List<Wallet> wallets) {
        System.out.println("\n---------------------------------");
        System.out.println(messages.get("report.wallets.byId.title"));
        System.out.println("---------------------------------");

        System.out.printf("%-10s | %-25s | %-20s%n", messages.get("report.wallet.table.id"), messages.get("report.wallet.table.holder"), messages.get("report.wallet.table.broker"));
        System.out.println("---------------------------------");

        for (Wallet wallet : wallets) {
            System.out.printf("%-10d | %-25s | %-20s%n", wallet.getId(), wallet.getHolder(), wallet.getBroker());
        }

        System.out.println("---------------------------------");
    }

    // Prints wallets sorted alphabetically by holder name.
    public void showWalletsOrderedByHolderReport(List<Wallet> wallets) {
        System.out.println("\n---------------------------------");
        System.out.println(messages.get("report.wallets.byHolder.title"));
        System.out.println("---------------------------------");

        System.out.printf("%-10s | %-25s | %-20s%n", messages.get("report.wallet.table.id"), messages.get("report.wallet.table.holder"), messages.get("report.wallet.table.broker"));
        System.out.println("---------------------------------");

        for (Wallet wallet : wallets) {
            System.out.printf("%-10d | %-25s | %-20s%n", wallet.getId(), wallet.getHolder(), wallet.getBroker());
        }

        System.out.println("---------------------------------");
    }

    // Prints the current balance of a single wallet.
    public void showWalletCurrentBalanceReport(Wallet wallet, double balance) {
        System.out.println("\n---------------------------------");
        System.out.println(messages.get("report.wallet.currentBalance.title"));
        System.out.println("---------------------------------");
        System.out.println(messages.get("report.wallet.currentBalance.id") + " " +  wallet.getId());
        System.out.println(messages.get("report.wallet.currentBalance.holder") + " " + wallet.getHolder());
        System.out.println(messages.get("report.wallet.currentBalance.broker") + " " + wallet.getBroker());
        System.out.printf(messages.get("report.wallet.currentBalance.balance") + " " + "%.2f%n", balance);
        System.out.println("---------------------------------");
    }

    // Prints the transaction history of a single wallet.
    public void showWalletHistoryReport(Wallet wallet, List<Transaction> transactions) {
        System.out.println("\n---------------------------------");
        System.out.println(messages.get("report.wallet.history.title"));
        System.out.println("---------------------------------");
        System.out.println(messages.get("report.wallet.history.id") + " " + wallet.getId());
        System.out.println(messages.get("report.wallet.history.holder") + " " + wallet.getHolder());
        System.out.println(messages.get("report.wallet.history.broker") + " " + wallet.getBroker());
        System.out.println("---------------------------------");
        System.out.printf("%-12s | %-10s | %-10s%n", messages.get("report.wallet.history.table.date"), messages.get("report.wallet.history.table.type"), messages.get("report.wallet.history.table.quantity"));
        System.out.println("---------------------------------");

        for (Transaction transaction : transactions) {
            String typeLabel = transaction.getOperationType() == OperationType.CASH_IN ? messages.get("report.wallet.history.type.buy") : messages.get("report.wallet.history.type.sell");
            System.out.printf("%-12s | %-10s | %-10.2f%n", DATE_FORMAT.format(transaction.getOperationDate()), typeLabel, transaction.getQuantity());
        }

        System.out.println("---------------------------------");
    }

    // Prints the gain or loss total for each wallet.
    public void showWalletGainLossReport(List<Wallet> wallets, TransactionDAO transactionDAO) {
        System.out.println("\n---------------------------------");
        System.out.println(messages.get("report.wallet.gainLoss.title"));
        System.out.println("---------------------------------");
        System.out.printf("%-10s | %-25s | %-12s%n", messages.get("report.wallet.gainLoss.table.id"), messages.get("report.wallet.gainLoss.table.holder"), messages.get("report.wallet.gainLoss.table.result"));
        System.out.println("---------------------------------");

        for (Wallet wallet : wallets) {
            double totalCashIn = 0.0;
            double totalCashOut = 0.0;

            for (Transaction transaction : transactionDAO.findByWalletId(wallet.getId())) {
                if (transaction.getOperationType() == OperationType.CASH_IN) {
                    totalCashIn += transaction.getQuantity();
                } else if (transaction.getOperationType() == OperationType.CASH_OUT) {
                    totalCashOut += transaction.getQuantity();
                }
            }

            double result = totalCashIn - totalCashOut;
            System.out.printf("%-10d | %-25s | %s%.2f%s%n", wallet.getId(), wallet.getHolder(), result < 0 ? RED : GREEN, result, RESET);
        }

        System.out.println("---------------------------------");
    }

    // Prints an error message highlighted in RED color
    public void showErrorMessage(String s) {
        System.out.println(RED + s + RESET);
    }

    // Prints a success message highlighted in GREEN color
    public void showSuccessMessage(String s) {
        System.out.println(GREEN + s + RESET);
    }

    // Prompts the user for a date, used for Oracle searches, edits and deletions.
    public Date readOracleDate() {
        Scanner read = ConsoleScanner.getInstance();

        // Loops until the user enters a valid date in the expected format
        while (true) {
            System.out.print(messages.get("report.oracle.date.prompt"));
            String input = read.nextLine();

            try {
                DATE_FORMAT.setLenient(false); // Rejects invalid dates like 32/13/2024
                return DATE_FORMAT.parse(input);
            } catch (ParseException e) {
                System.out.println(RED + messages.get("report.oracle.date.invalid") + RESET);
            }
        }
    }

    // Reads all fields required to register a new Oracle quote.
    public Oracle readOracleData() {
        Scanner read = ConsoleScanner.getInstance();

        Date date = readOracleDate();

        System.out.print(messages.get("report.oracle.price.prompt"));
        double price = read.nextDouble();
        read.nextLine();

        while (price <= 0) {
            System.out.print(messages.get("report.oracle.price.invalid"));
            price = read.nextDouble();
            read.nextLine();
        }

        return new Oracle(date, price);
    }

    // Displays the details of a single Oracle quote on the console.
    public void displayOracle(Oracle oracle) {
        System.out.println("\n" + messages.get("report.oracle.display.title"));
        System.out.println(messages.get("report.oracle.display.date") + " " + DATE_FORMAT.format(oracle.getDate()));
        System.out.printf(messages.get("report.oracle.display.price") + " " + "%.2f%n", oracle.getPrice());
        System.out.println(messages.get("report.oracle.display.separator"));
    }

    // Iterates through a list of Oracle quotes and prints them formatted to the console.
    public void displayOracleList(List<Oracle> oracles) {
        System.out.println("\n" + messages.get("report.oracle.list.title"));
        for (int i = 0; i < oracles.size(); i++) {
            Oracle o = oracles.get(i);
            System.out.println(messages.get("report.oracle.list.separator"));
            System.out.println(messages.get("report.oracle.list.item") + " " + (i + 1));
            System.out.println(messages.get("report.oracle.list.date") + " " + DATE_FORMAT.format(o.getDate()));
            System.out.printf(messages.get("report.oracle.list.price") + " " + "%.2f%n", o.getPrice());
        }
        System.out.println(messages.get("report.oracle.list.separator"));
    }

    // Prompts the user for an updated price, maintaining the original date.
    public Oracle readOracleUpdates(Oracle editableOracle) {
        Scanner read = ConsoleScanner.getInstance();

        System.out.println(messages.get("report.oracle.update.dateCurrent") + " " + DATE_FORMAT.format(editableOracle.getDate()));
        System.out.printf(messages.get("report.oracle.update.priceCurrent") + " " + "%.2f%n", editableOracle.getPrice());

        System.out.print(messages.get("report.oracle.update.pricePrompt"));
        double price = read.nextDouble();
        read.nextLine();

        while (price <= 0) {
            System.out.print(messages.get("report.oracle.price.invalid"));
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
            System.out.println(messages.get("report.oracle.confirmDelete") + DATE_FORMAT.format(removableOracle.getDate()) + messages.get("report.oracle.confirmDelete.suffix"));
            System.out.println("1 - " + messages.get("generic.confirmYes"));
            System.out.println("2 - " + messages.get("generic.confirmNo"));

            int option = read.nextInt();
            read.nextLine();

            switch (option) {
                case 1:
                    return true;
                case 2:
                    return false;
                default:
                    System.out.println(messages.get("generic.confirmInvalid"));
                    break;
            }
        }
    }
}