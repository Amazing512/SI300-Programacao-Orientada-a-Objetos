package org.unicamp.poo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.enums.OperationType;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import static org.unicamp.poo.util.ConsoleColors.YELLOW;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.Menu;
import org.unicamp.poo.view.TransactionView;

// Controller responsável por lidar com operações de compra, venda e histórico de transações de moedas.
public class TransactionController {

    final TransactionDAO model;
    final WalletDAO walletModel;
    final OracleController oracleController;
    final TransactionView view;
    final MessageProvider messages;

    public TransactionController(TransactionDAO model, WalletDAO walletModel, OracleController oracleController, TransactionView view, MessageProvider messages) {
        super();
        this.model = model;
        this.walletModel = walletModel;
        this.oracleController = oracleController;
        this.view = view;
        this.messages = messages;
    }

    // Calcula o saldo atual de uma carteira com base em seu histórico de transações.
    private double calculateWalletBalance (int walletId){
        List<Transaction> transactions = model.findByWalletId(walletId);
        double balance = 0.0;

        for (Transaction t : transactions){
            if (t.getOperationType() == OperationType.CASH_IN) {
                balance += t.getQuantity();
            }
            else if (t.getOperationType() == OperationType.CASH_OUT){
                balance -= t.getQuantity();
            }
        }
        return balance;
    }

    private void actionBuyCoin() {
        Oracle dailyQuote = oracleController.getOrGenerateDailyQuote();
        if (dailyQuote == null) {
            view.showErrorMessage(messages.get("transaction.oracle.notAvailable"));
            return;
        }
        view.displayDailyQuote(dailyQuote);

        Transaction newTransaction = view.readTransactionData(OperationType.CASH_IN);

        if (newTransaction != null){
            if (newTransaction.getQuantity() <= 0){
                view.showErrorMessage(messages.get("transaction.error.negativeValue"));
                return;
            }

            var wallet = walletModel.findById(newTransaction.getWalletId());
            if (wallet == null) {
                view.showErrorMessage(messages.get("transaction.wallet.notFound"));
                return;
            }

            double totalValue = newTransaction.getQuantity() * dailyQuote.getPrice();
            String confirmMsg = messages.get("transaction.confirm.buy.part1") + " "
                              + String.format("%.4f", newTransaction.getQuantity()) + " "
                              + messages.get("transaction.confirm.buy.part2") + " "
                              + String.format("%.2f", totalValue)
                              + "?";

            if (view.confirmRejectTransaction(confirmMsg)) {
                view.showErrorMessage(messages.get("transaction.cancelled"));
                return;
            }

            Transaction savedTransaction = model.create(newTransaction);
            if (savedTransaction != null) {
                view.showSuccessMessage(messages.get("transaction.buy.success"));
            }
            else {
                view.showErrorMessage(messages.get("transaction.buy.error"));
            }
        } else {
            view.showErrorMessage(messages.get("transaction.cancelled"));
        }
    }

    private void actionSellCoin() {
        Oracle dailyQuote = oracleController.getOrGenerateDailyQuote();
        if (dailyQuote == null) {
            view.showErrorMessage(messages.get("transaction.oracle.notAvailable"));
            return;
        }
        view.displayDailyQuote(dailyQuote);

        int walletId = view.readWalletId();

        var wallet = walletModel.findById(walletId);
        if (wallet == null){
            view.showErrorMessage(messages.get("transaction.wallet.notFound"));
            return;
        }

        double currentBalance = calculateWalletBalance(walletId);
        view.displayWalletBalance(currentBalance);

        Double quantity = view.readQuantity();
        if (quantity == null) {
            view.showErrorMessage(messages.get("transaction.cancelled"));
            return;
        }

        if (quantity > currentBalance) {
            view.showErrorMessage(messages.get("transaction.sell.insufficientBalance"));
            return;
        }

        Transaction newTransaction = new Transaction(walletId, new Date(), OperationType.CASH_OUT, quantity);

        double totalValue = newTransaction.getQuantity() * dailyQuote.getPrice();
        String confirmMsg = messages.get("transaction.confirm.sell.part1") + " "
                          + String.format("%.4f", newTransaction.getQuantity()) + " "
                          + messages.get("transaction.confirm.sell.part2") + " "
                          + String.format("%.2f", totalValue)
                          + "?";

        if (view.confirmRejectTransaction(confirmMsg)) {
            view.showErrorMessage(messages.get("transaction.cancelled"));
            return;
        }

        Transaction savedTransaction = model.create(newTransaction);
        if (savedTransaction != null) {
            view.showSuccessMessage(messages.get("transaction.sell.success"));
        }
        else {
            view.showErrorMessage(messages.get("transaction.sell.error"));
        }
    }

    private List<String> getMenuOptions()
    {
        final List<String> options = new ArrayList<>();
        options.add(messages.get("transactionMenu.return"));
        options.add(messages.get("transactionMenu.buyCoin"));
        options.add(messages.get("transactionMenu.sellCoin"));
        return (options);
    }

    public void start()
    {
        final List<String> options = getMenuOptions();
        final Menu transactionMenu = new Menu(ConsoleScanner.getInstance(), messages);
        boolean loop = true;

        while (loop) {
            String yellowTitle = YELLOW + messages.get("transactionMenu.title") + RESET;

            switch (transactionMenu.getChoice(yellowTitle, options, messages.get("transactionMenu.prompt")))
            {
                case 0 -> loop = false;
                case 1 -> {
                    ConsoleScanner.clearScreen();
                    actionBuyCoin();
                    ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
                    ConsoleScanner.clearScreen();
                }
                case 2 -> {
                    ConsoleScanner.clearScreen();
                    actionSellCoin();
                    ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
                    ConsoleScanner.clearScreen();
                }
                default -> loop = false;
            }
        }
    }
}
