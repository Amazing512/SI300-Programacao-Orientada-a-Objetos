package org.unicamp.poo.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.enums.OperationType;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import static org.unicamp.poo.util.ConsoleColors.YELLOW;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.Menu;
import org.unicamp.poo.view.ReportView;

/*  Controller responsible for generating consolidated financial reports and
    sorting wallet lists based on their current balance. */

public class ReportController {

    private final WalletDAO walletDAO;
    private final TransactionDAO transactionDAO;
    private final OracleController oracleController;
    private final ReportView view;
    private final MessageProvider messages;

    // Initializes the report controller with required DAOs, view, and internationalization provider.
    public ReportController(WalletDAO walletDAO, TransactionDAO transactionDAO, OracleController oracleController, ReportView view, MessageProvider messages) {
        super();
        this.walletDAO = walletDAO;
        this.transactionDAO = transactionDAO;
        this.oracleController = oracleController;
        this.view = view;
        this.messages = messages;
    }

    // Dynamically calculates the total current balance of a specific wallet.
    private double calculateWalletBalance (int walletId){
        List<Transaction> transactions = transactionDAO.findByWalletId(walletId);
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

    // Processes the financial report data flow for a specific wallet.
    public void showFinancialReport(){
        int walletId = view.readWalletIdReport();

        // Ensure the wallet exists before fetching transactions
        Wallet wallet = walletDAO.findById(walletId);
        if (wallet == null){
            view.showErrorMessage(messages.get("transaction.wallet.notFound"));
            return;
        }

        List<Transaction> transactions = transactionDAO.findByWalletId(walletId);
        double totalCoinsBought = 0.0;
        double totalCoinsSold = 0.0;
        double totalMoneySpent = 0.0;
        double totalMoneyReceived = 0.0;

        for (Transaction t : transactions) {
            double price = 0.0;
            Oracle quote = oracleController.findByDate(t.getOperationDate());

            if (quote != null) {
                price = quote.getPrice();
            } else {
                // fallback to today's price if no quote exists for transaction date
                Oracle todayQuote = oracleController.getOrGenerateDailyQuote();
                if (todayQuote != null) {
                    price = todayQuote.getPrice();
                }
            }

            double value = t.getQuantity() * price;

            if (t.getOperationType() == OperationType.CASH_IN) {
                totalCoinsBought += t.getQuantity();
                totalMoneySpent += value;
            } else if (t.getOperationType() == OperationType.CASH_OUT) {
                totalCoinsSold += t.getQuantity();
                totalMoneyReceived += value;
            }
        }

        double coinBalance = totalCoinsBought - totalCoinsSold;

        // Current value of the remaining coins (coinBalance * today's price)
        double currentPrice = 0.0;
        Oracle todayQuote = oracleController.getOrGenerateDailyQuote();
        if (todayQuote != null) {
            currentPrice = todayQuote.getPrice();
        }
        double currentHoldingsValue = coinBalance * currentPrice;

        // Financial Profit / Loss = (Current Value of Remaining Coins + Money Received from Sales) - Money Spent on Purchases
        double totalFinancialGainLoss = (currentHoldingsValue + totalMoneyReceived) - totalMoneySpent;

        // Delegates the visual presentation to the view layer
        view.showFinancialReport(
            walletId,
            totalCoinsBought,
            totalCoinsSold,
            coinBalance,
            totalMoneySpent,
            totalMoneyReceived,
            currentHoldingsValue,
            totalFinancialGainLoss
        );
    }

    // Lists wallets ordered by identifier in ascending order.
    private void showWalletsOrderedById() {
        List<Wallet> wallets = walletDAO.findAll();

        if (wallets == null || wallets.isEmpty()) {
            view.showErrorMessage(messages.get("report.wallets.empty"));
            return;
        }

        wallets.sort(Comparator.comparingInt(Wallet::getId));
        view.showWalletsOrderedByIdReport(wallets);
    }

    // Lists wallets ordered alphabetically by holder name.
    private void showWalletsOrderedByHolder() {
        List<Wallet> wallets = walletDAO.findAllOrderByHolder();

        if (wallets == null || wallets.isEmpty()) {
            view.showErrorMessage(messages.get("report.wallets.empty"));
            return;
        }

        view.showWalletsOrderedByHolderReport(wallets);
    }

    // Shows the current balance of a single wallet.
    private void showWalletCurrentBalance() {
        int walletId = view.readWalletIdReport(messages.get("report.wallet.balance.prompt"));
        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            view.showErrorMessage(messages.get("transaction.wallet.notFound"));
            return;
        }

        double balance = calculateWalletBalance(walletId);
        view.showWalletCurrentBalanceReport(wallet, balance);
    }

    // Shows the transaction history of a single wallet.
    private void showWalletHistory() {
        int walletId = view.readWalletIdReport(messages.get("report.wallet.history.prompt"));
        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            view.showErrorMessage(messages.get("transaction.wallet.notFound"));
            return;
        }

        List<Transaction> transactions = transactionDAO.findByWalletId(walletId);
        if (transactions == null || transactions.isEmpty()) {
            view.showErrorMessage(messages.get("report.wallet.history.empty"));
            return;
        }

        view.showWalletHistoryReport(wallet, transactions);
    }

    // Shows the gain or loss total for each wallet.
    private void showWalletGainOrLoss() {
        List<Wallet> wallets = walletDAO.findAll();

        if (wallets == null || wallets.isEmpty()) {
            view.showErrorMessage(messages.get("report.wallets.empty"));
            return;
        }

        view.showWalletGainLossReport(wallets, transactionDAO);
    }

    // Generates the list of translated menu options for the reports section.
    private List<String> getMenuOptions(){
        final List<String> options = new ArrayList<>();
        options.add(messages.get("reportMenu.return"));
        options.add(messages.get("reportMenu.financial"));
        options.add(messages.get("reportMenu.sortedWalletsById"));
        options.add(messages.get("reportMenu.sortedWalletsByHolder"));
        options.add(messages.get("reportMenu.walletBalance"));
        options.add(messages.get("reportMenu.walletHistory"));
        options.add(messages.get("reportMenu.walletGainLoss"));

        return options;
    }

    // Starts the main sub menu loop navigation for reports.
    public void start() {
        final List<String> options = getMenuOptions();
        final Menu reportMenu = new Menu(ConsoleScanner.getInstance(), messages);
        boolean loop = true;

        while (loop) {
            String yellowTitle = YELLOW + messages.get("reportMenu.title") + RESET;

            switch (reportMenu.getChoice(yellowTitle, options, messages.get("reportMenu.prompt"))) {
                case 0 -> loop = false;
                case 1 -> showFinancialReport();
                case 2 -> showWalletsOrderedById();
                case 3 -> showWalletsOrderedByHolder();
                case 4 -> showWalletCurrentBalance();
                case 5 -> showWalletHistory();
                case 6 -> showWalletGainOrLoss();
                default -> loop = false;
            }
        }
    }

}
