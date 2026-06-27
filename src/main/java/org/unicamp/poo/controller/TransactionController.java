package org.unicamp.poo.controller;

import java.util.ArrayList;
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

// Controller responsible for handling coin buy, sell, and transaction history operations.

public class TransactionController {

    // Model dependencies for persistence and business logic
    TransactionDAO model;
    WalletDAO walletModel;
    OracleController oracleController;
    TransactionView view;

    MessageProvider messages;

    // Initializes the controller with necessary DAOs, views, the internal Oracle quote
    // provider, and internationalization tools.

    public TransactionController(TransactionDAO model, WalletDAO walletModel, OracleController oracleController, TransactionView view, MessageProvider messages) {
        super();
        this.model = model;
        this.walletModel = walletModel;
        this.oracleController = oracleController;
        this.view = view;
        this.messages = messages;
    }

    // Dynamically calculates the current balance of a wallet based on its transaction history.

    private double calculateWalletBalance (int walletId){
        List<Transaction> transactions = model.findByWalletId(walletId);
        double balance = 0.0;

        for (Transaction t : transactions){
            if (t.getOperationType() == OperationType.CASH_IN) {
                balance += t.getQuantity(); // Increases balance on buy
            }
            else if (t.getOperationType() == OperationType.CASH_OUT){
                balance -= t.getQuantity(); // Decreases balance on sell
            }
        }
        return balance;
    }

    private void actionBuyCoin() {
        // Consults the internal Oracle for today's quote before showing the buy form.
        // The operation is blocked if no quote is available, since the price
        // displayed to the user is the basis for the decision to buy.
        Oracle dailyQuote = oracleController.getOrGenerateDailyQuote();
        if (dailyQuote == null) {
            view.showErrorMessage(messages.get("transaction.oracle.notAvailable"));
            return;
        }
        view.displayDailyQuote(dailyQuote);

        Transaction newTransaction = view.readTransactionData(OperationType.CASH_IN);

        if (newTransaction != null){
            // Ensure the transaction amount is strictly positive
            if (newTransaction.getQuantity() <= 0){
                view.showErrorMessage(messages.get("transaction.error.negativeValue"));
                return;
            }

            // Check if the targeted wallet actually exists
            var wallet = walletModel.findById(newTransaction.getWalletId());
            if (wallet == null) {
                view.showErrorMessage(messages.get("transaction.wallet.notFound"));
                return;
            }

            // Persist the buy transaction
            Transaction savedTransaction = model.create(newTransaction);
            if (savedTransaction != null) {
                view.showSuccessMessage(messages.get("transaction.buy.success"));
            }
            else {
                view.showErrorMessage(messages.get("transaction.buy.error"));
            }
        }
    }

    private void actionSellCoin() {
        // Consults the internal Oracle for today's quote before showing the sell form.
        // The operation is blocked if no quote is available, since the price
        // displayed to the user is the basis for the decision to sell.
        Oracle dailyQuote = oracleController.getOrGenerateDailyQuote();
        if (dailyQuote == null) {
            view.showErrorMessage(messages.get("transaction.oracle.notAvailable"));
            return;
        }
        view.displayDailyQuote(dailyQuote);

        Transaction newTransaction = view.readTransactionData(OperationType.CASH_OUT);

        if (newTransaction != null){
            // Ensure the transaction amount is strictly positive
            if (newTransaction.getQuantity() <= 0){
                view.showErrorMessage(messages.get("transaction.error.negativeValue"));
                return;
            }

            // Check if the targeted wallet actually exists
            var wallet = walletModel.findById(newTransaction.getWalletId());
            if (wallet == null){
                view.showErrorMessage(messages.get("transaction.wallet.notFound"));
                return;
            }

            // Ensure the wallet has enough funds for the operation
            double currentBalance = calculateWalletBalance(newTransaction.getWalletId());
            if (newTransaction.getQuantity() > currentBalance) {
                view.showErrorMessage(messages.get("transaction.sell.insufficientBalance"));
                return;
            }

            // Persist the sell transaction
            Transaction savedTransaction = model.create(newTransaction);
            if (savedTransaction != null) {
                view.showSuccessMessage(messages.get("transaction.sell.success"));
            }
            else {
                view.showErrorMessage(messages.get("transaction.sell.error"));
            }
        }
    }


    // Generates the translated text options for the transaction menu.

    private List<String> getMenuOptions()
    {
        final List<String> options = new ArrayList<>();
        options.add(messages.get("transactionMenu.return"));
        options.add(messages.get("transactionMenu.buyCoin"));
        options.add(messages.get("transactionMenu.sellCoin"));
        return (options);
    }

    // Drives the main transaction sub-menu loop.

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
                case 1 -> actionBuyCoin();
                case 2 -> actionSellCoin();
                default -> loop = false;
            }
        }
    }
}
