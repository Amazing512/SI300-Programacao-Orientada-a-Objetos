package org.unicamp.poo.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.enums.OperationType;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.Menu;
import org.unicamp.poo.view.TransactionView;

public class TransactionController {
    private final TransactionDAO transactionDAO;
    private final WalletDAO walletDAO;
    private final OracleDAO oracleDAO;
    private final TransactionView transactionView;
    private final MessageProvider messages;

    public TransactionController(TransactionDAO transactionDAO, WalletDAO walletDAO, OracleDAO oracleDAO, TransactionView transactionView, MessageProvider messages) {
        this.transactionDAO = transactionDAO;
        this.walletDAO = walletDAO;
        this.oracleDAO = oracleDAO;
        this.transactionView = transactionView;
        this.messages = messages;
    }

    public void start() {
        Menu menu = new Menu();
        boolean back = false;
        List<String> options = List.of(
                messages.get("transactionMenu.return"),
                messages.get("transactionMenu.buyCoin"),
                messages.get("transactionMenu.sellCoin")
        );

        while (!back) {
            int choice = menu.getChoice(
                    messages.get("transactionMenu.title"),
                    options,
                    messages.get("transactionMenu.prompt")
            );

            switch (choice) {
                case 0 -> back = true;
                case 1 -> buyVirtualCoin();
                case 2 -> sellVirtualCoin();
                default -> transactionView.showErrorMessage(messages.get("generic.confirmInvalid"));
            }
        }
    }

    private void buyVirtualCoin() {
        int walletId = transactionView.readWalletId();
        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            transactionView.showErrorMessage(messages.get("transaction.wallet.notFound"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        Date targetDate = transactionView.readTransactionDate();
        Oracle dailyQuote = oracleDAO.findByDate(targetDate);

        if (dailyQuote == null) {
            transactionView.showErrorMessage(messages.get("report.oracle.notFound"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        transactionView.displayDailyQuote(dailyQuote);
        double coinBalance = getWalletCoinBalance(walletId);
        transactionView.displayWalletBalance(coinBalance);

        Double quantity = transactionView.readQuantity();
        if (quantity == null) {
            transactionView.showErrorMessage(messages.get("transaction.cancelled"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        double totalCost = quantity * dailyQuote.getPrice();
        String confirmMsg = messages.get("transaction.confirm.buy.part1") + " " + quantity + " " +
                messages.get("transaction.confirm.buy.part2") + String.format("%.2f", totalCost) + "?";

        if (transactionView.confirmRejectTransaction(confirmMsg)) {
            transactionView.showErrorMessage(messages.get("transaction.cancelled"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        Transaction transaction = new Transaction(walletId, targetDate, OperationType.CASH_IN, quantity);
        boolean success = transactionDAO.create(transaction);

        if (success) {
            transactionView.showSuccessMessage(messages.get("transaction.buy.success"));
        } else {
            transactionView.showErrorMessage(messages.get("transaction.buy.error"));
        }

        ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
    }

    private void sellVirtualCoin() {
        int walletId = transactionView.readWalletId();
        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            transactionView.showErrorMessage(messages.get("transaction.wallet.notFound"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        double coinBalance = getWalletCoinBalance(walletId);
        if (coinBalance <= 0) {
            transactionView.showErrorMessage(messages.get("transaction.sell.insufficientBalance"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        Date targetDate = transactionView.readTransactionDate();
        Oracle dailyQuote = oracleDAO.findByDate(targetDate);

        if (dailyQuote == null) {
            transactionView.showErrorMessage(messages.get("report.oracle.notFound"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        transactionView.displayDailyQuote(dailyQuote);
        transactionView.displayWalletBalance(coinBalance);

        Double quantity = transactionView.readQuantity();
        if (quantity == null) {
            transactionView.showErrorMessage(messages.get("transaction.cancelled"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        if (quantity > coinBalance) {
            transactionView.showErrorMessage(messages.get("transaction.sell.insufficientBalance"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        double totalRevenue = quantity * dailyQuote.getPrice();
        String confirmMsg = messages.get("transaction.confirm.sell.part1") + " " + quantity + " " +
                messages.get("transaction.confirm.sell.part2") + String.format("%.2f", totalRevenue) + "?";

        if (transactionView.confirmRejectTransaction(confirmMsg)) {
            transactionView.showErrorMessage(messages.get("transaction.cancelled"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        Transaction transaction = new Transaction(walletId, targetDate, OperationType.CASH_OUT, quantity);
        boolean success = transactionDAO.create(transaction);

        if (success) {
            transactionView.showSuccessMessage(messages.get("transaction.sell.success"));
        } else {
            transactionView.showErrorMessage(messages.get("transaction.sell.error"));
        }

        ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
    }

    private double getWalletCoinBalance(int walletId) {
        List<Transaction> transactions = getTransactionsByWallet(walletId);
        double balance = 0.0;

        for (Transaction t : transactions) {
            if (t.getOperationType() == OperationType.CASH_IN) {
                balance += t.getQuantity();
            } else if (t.getOperationType() == OperationType.CASH_OUT) {
                balance -= t.getQuantity();
            }
        }
        return balance;
    }

    private List<Transaction> getTransactionsByWallet(int walletId) {
        return transactionDAO.findByWalletId(walletId).stream()
                .filter(t -> t.getWalletId() == walletId)
                .collect(Collectors.toList());
    }
}
