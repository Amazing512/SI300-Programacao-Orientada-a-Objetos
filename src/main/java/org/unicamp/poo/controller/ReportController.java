package org.unicamp.poo.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final WalletDAO walletDAO;
    private final TransactionDAO transactionDAO;
    private final OracleController oracleController;
    private final ReportView view;
    private final MessageProvider messages;

    public ReportController(
        WalletDAO walletDAO,
        TransactionDAO transactionDAO,
        OracleController oracleController,
        ReportView view,
        MessageProvider messages
    ) {
        super();
        this.walletDAO = walletDAO;
        this.transactionDAO = transactionDAO;
        this.oracleController = oracleController;
        this.view = view;
        this.messages = messages;
    }

    private double calculateWalletBalance (int walletId){
        List<Transaction> transactions = transactionDAO.findByWalletId(walletId);
        double balance = 0.0;

        for (Transaction transaction : transactions){
            if (transaction.getOperationType() == OperationType.CASH_IN) {
                balance += transaction.getQuantity();
            }
            else if (transaction.getOperationType() == OperationType.CASH_OUT){
                balance -= transaction.getQuantity();
            }
        }
        return balance;
    }

    private double getTodayPrice() {
        Oracle todayQuote = oracleController.getOrGenerateDailyQuote();
        return (todayQuote != null) ? todayQuote.getPrice() : 0.0;
    }

    private record LedgerRow(double price, double coinBalance, double averageCost, double operationResult, double accumulatedResult) {}

    private List<LedgerRow> buildWalletLedger(List<Transaction> sortedTransactions) {
        double todayPrice = getTodayPrice();
        double coinBalance = 0.0;
        double averageCost = 0.0;
        double accumulatedResult = 0.0;
        List<LedgerRow> ledger = new ArrayList<>();

        for (Transaction transaction : sortedTransactions) {
            Oracle quote = oracleController.findByDate(transaction.getOperationDate());
            double price = (quote != null) ? quote.getPrice() : todayPrice;
            double operationResult = 0.0;

            if (transaction.getOperationType() == OperationType.CASH_IN) {
                double purchaseValue = transaction.getQuantity() * price;
                averageCost = ((averageCost * coinBalance) + purchaseValue) / (coinBalance + transaction.getQuantity());
                coinBalance += transaction.getQuantity();
            } else if (transaction.getOperationType() == OperationType.CASH_OUT) {
                operationResult = transaction.getQuantity() * (price - averageCost);
                accumulatedResult += operationResult;
                coinBalance -= transaction.getQuantity();
            }

            ledger.add(new LedgerRow(price, coinBalance, averageCost, operationResult, accumulatedResult));
        }

        return ledger;
    }

    public void showFinancialReport(){
        int walletId = view.readWalletIdReport();
        log.info("Solicitada geração de relatório financeiro para carteira ID {}", walletId);

        Wallet wallet = walletDAO.findById(walletId);
        if (wallet == null){
            log.warn("Relatório financeiro cancelado: carteira ID {} não encontrada", walletId);
            view.showErrorMessage(messages.get("transaction.wallet.notFound"));
            return;
        }

        List<Transaction> transactions = transactionDAO.findByWalletId(walletId);
        double totalCoinsBought = 0.0;
        double totalCoinsSold = 0.0;
        double totalMoneySpent = 0.0;
        double totalMoneyReceived = 0.0;

        double todayPrice = 0.0;
        Oracle todayQuote = oracleController.getOrGenerateDailyQuote();
        if (todayQuote != null) {
            todayPrice = todayQuote.getPrice();
        }

        for (Transaction transaction : transactions) {
            double price;
            Oracle quote = oracleController.findByDate(transaction.getOperationDate());

            if (quote != null) {
                price = quote.getPrice();
            } else {
                price = todayPrice;
            }

            double value = transaction.getQuantity() * price;

            if (transaction.getOperationType() == OperationType.CASH_IN) {
                totalCoinsBought += transaction.getQuantity();
                totalMoneySpent += value;
            } else if (transaction.getOperationType() == OperationType.CASH_OUT) {
                totalCoinsSold += transaction.getQuantity();
                totalMoneyReceived += value;
            }
        }

        double coinBalance = totalCoinsBought - totalCoinsSold;

        double currentHoldingsValue = coinBalance * todayPrice;

        double totalFinancialGainLoss = (currentHoldingsValue + totalMoneyReceived) - totalMoneySpent;

        log.info("Relatório financeiro gerado com sucesso para carteira ID {}: resultado acumulado=R$ {}",
                walletId, totalFinancialGainLoss);

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

    private void showWalletsOrderedById() {
        log.info("Gerando relatório de carteiras ordenadas por ID");
        List<Wallet> wallets = walletDAO.findAll();

        if (wallets == null || wallets.isEmpty()) {
            log.warn("Nenhuma carteira encontrada para listagem por ID.");
            view.showErrorMessage(messages.get("report.wallets.empty"));
            return;
        }

        wallets.sort(Comparator.comparingInt(Wallet::getId));
        view.showWalletsOrderedByIdReport(wallets);
    }

    private void showWalletsOrderedByHolder() {
        log.info("Gerando relatório de carteiras ordenadas por titular");
        List<Wallet> wallets = walletDAO.findAllOrderByHolder();

        if (wallets == null || wallets.isEmpty()) {
            log.warn("Nenhuma carteira encontrada para listagem por titular.");
            view.showErrorMessage(messages.get("report.wallets.empty"));
            return;
        }

        view.showWalletsOrderedByHolderReport(wallets);
    }

    private void showWalletCurrentBalance() {
        int walletId = view.readWalletIdReport(messages.get("report.wallet.balance.prompt"));
        log.info("Consultando saldo atual da carteira ID {}", walletId);
        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            log.warn("Consulta de saldo cancelada: carteira ID {} não encontrada", walletId);
            view.showErrorMessage(messages.get("transaction.wallet.notFound"));
            return;
        }

        double balance = calculateWalletBalance(walletId);
        log.info("Saldo da carteira ID {}: {}", walletId, balance);
        view.showWalletCurrentBalanceReport(wallet, balance);
    }

    private void showWalletHistory() {
        int walletId = view.readWalletIdReport(messages.get("report.wallet.history.prompt"));
        log.info("Consultando histórico de transações da carteira ID {}", walletId);
        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            log.warn("Consulta de histórico cancelada: carteira ID {} não encontrada", walletId);
            view.showErrorMessage(messages.get("transaction.wallet.notFound"));
            return;
        }

        List<Transaction> transactions = transactionDAO.findByWalletId(walletId);
        if (transactions == null || transactions.isEmpty()) {
            log.warn("Nenhuma transação encontrada no histórico da carteira ID {}", walletId);
            view.showErrorMessage(messages.get("report.wallet.history.empty"));
            return;
        }

        List<Double> cashValues = new ArrayList<>();

        double todayPrice = 0.0;

        Oracle todayQuote = oracleController.getOrGenerateDailyQuote();
        if (todayQuote != null) {
            todayPrice = todayQuote.getPrice();
        }

        for (Transaction transaction : transactions) {
            double price = todayPrice;
            Oracle quote = oracleController.findByDate(transaction.getOperationDate());
            if (quote != null) {
                price = quote.getPrice();
            }
            double value = transaction.getQuantity() * price;
            cashValues.add(value);
        }

        view.showWalletHistoryReport(wallet, transactions, cashValues);
    }

    private void showWalletGainOrLoss() {
        int walletId = view.readWalletIdReport(messages.get("report.wallet.history.prompt"));
        log.info("Calculando ganho/perda de capital para carteira ID {}", walletId);
        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            log.warn("Cálculo de ganho/perda cancelado: carteira ID {} não encontrada", walletId);
            view.showErrorMessage(messages.get("transaction.wallet.notFound"));
            return;
        }

        List<Transaction> transactions = new ArrayList<>(transactionDAO.findByWalletId(walletId));
        transactions.sort(Comparator.comparing(Transaction::getOperationDate).thenComparing(Transaction::getId));

        if (transactions.isEmpty()) {
            log.warn("Nenhuma transação disponível para cálculo de ganho/perda na carteira ID {}", walletId);
            view.showErrorMessage(messages.get("report.wallet.history.empty"));
            return;
        }

        List<LedgerRow> ledger = buildWalletLedger(transactions);

        List<Double> prices = new ArrayList<>();
        List<Double> coinBalances = new ArrayList<>();
        List<Double> averageCosts = new ArrayList<>();
        List<Double> operationResults = new ArrayList<>();
        List<Double> accumulatedResults = new ArrayList<>();

        for (LedgerRow row : ledger) {
            prices.add(row.price());
            coinBalances.add(row.coinBalance());
            averageCosts.add(row.averageCost());
            operationResults.add(row.operationResult());
            accumulatedResults.add(row.accumulatedResult());
        }

        view.showWalletGainLossReport(wallet, transactions, prices, coinBalances, averageCosts, operationResults, accumulatedResults);
    }

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

    public void start() {
        final List<String> options = getMenuOptions();
        final Menu reportMenu = new Menu(ConsoleScanner.getInstance(), messages);
        boolean loop = true;

        while (loop) {
            String yellowTitle = YELLOW + messages.get("reportMenu.title") + RESET;

            switch (reportMenu.getChoice(yellowTitle, options, messages.get("reportMenu.prompt"))) {
                case 0 -> loop = false;
                case 1 -> {
                    ConsoleScanner.clearScreen();
                    showFinancialReport();
                    ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
                    ConsoleScanner.clearScreen();
                }
                case 2 -> {
                    ConsoleScanner.clearScreen();
                    showWalletsOrderedById();
                    ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
                    ConsoleScanner.clearScreen();
                }
                case 3 -> {
                    ConsoleScanner.clearScreen();
                    showWalletsOrderedByHolder();
                    ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
                    ConsoleScanner.clearScreen();
                }
                case 4 -> {
                    ConsoleScanner.clearScreen();
                    showWalletCurrentBalance();
                    ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
                    ConsoleScanner.clearScreen();
                }
                case 5 -> {
                    ConsoleScanner.clearScreen();
                    showWalletHistory();
                    ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
                    ConsoleScanner.clearScreen();
                }
                case 6 -> {
                    ConsoleScanner.clearScreen();
                    showWalletGainOrLoss();
                    ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
                    ConsoleScanner.clearScreen();
                }
                default -> loop = false;
            }
        }
    }

}
