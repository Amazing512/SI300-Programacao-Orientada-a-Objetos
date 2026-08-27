package org.unicamp.poo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
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
import org.unicamp.poo.view.ReportView;

public class ReportController {
    private final WalletDAO walletDAO;
    private final TransactionDAO transactionDAO;
    private final OracleDAO oracleDAO;
    private final ReportView reportView;
    private final MessageProvider messages;

    public ReportController(WalletDAO walletDAO, TransactionDAO transactionDAO, OracleDAO oracleDAO, ReportView reportView, MessageProvider messages) {
        this.walletDAO = walletDAO;
        this.transactionDAO = transactionDAO;
        this.oracleDAO = oracleDAO;
        this.reportView = reportView;
        this.messages = messages;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        Menu menu = new Menu();        boolean back = false;
        List<String> options = List.of(
                messages.get("reportMenu.return"),
                messages.get("reportMenu.financial"),
                messages.get("reportMenu.sortedWalletsById"),
                messages.get("reportMenu.sortedWalletsByHolder"),
                messages.get("reportMenu.walletBalance"),
                messages.get("reportMenu.walletHistory"),
                messages.get("reportMenu.walletGainLoss")
        );

        while (!back) {
            int choice = menu.getChoice(
                    messages.get("reportMenu.title"),
                    options,
                    messages.get("reportMenu.prompt")
            );

            switch (choice) {
                case 0 -> back = true;
                case 1 -> showApurationResultsReport();
                case 2 -> showWalletsOrderedByIdReport();
                case 3 -> showWalletsOrderedByHolderReport();
                case 4 -> showWalletCurrentBalanceReport();
                case 5 -> showWalletHistoryReport();
                case 6 -> showWalletGainLossReport();
                default -> reportView.showErrorMessage(messages.get("generic.confirmInvalid"));
            }
        }
    }

    private void showWalletsOrderedByIdReport() {
        List<Wallet> wallets = walletDAO.findAllOrderByHolder();
        if (wallets.isEmpty()) {
            reportView.showErrorMessage(messages.get("report.wallets.empty"));
        } else {
            reportView.showWalletsOrderedByIdReport(wallets);
        }
        ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
    }

    private void showWalletsOrderedByHolderReport() {
        List<Wallet> wallets = walletDAO.findAllOrderByHolder();
        if (wallets.isEmpty()) {
            reportView.showErrorMessage(messages.get("report.wallets.empty"));
        } else {
            reportView.showWalletsOrderedByHolderReport(wallets);
        }
        ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
    }

    private void showWalletCurrentBalanceReport() {
        int walletId = reportView.readWalletIdReport(messages.get("report.wallet.balance.prompt"));
        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            reportView.showErrorMessage(messages.get("transaction.wallet.notFound"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        List<Transaction> transactions = getTransactionsByWallet(walletId);
        double balance = 0;
        for (Transaction t : transactions) {
            if (t.getOperationType() == OperationType.CASH_IN) {
                balance += t.getQuantity();
            } else {
                balance -= t.getQuantity();
            }
        }

        reportView.showWalletCurrentBalanceReport(wallet, balance);
        ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
    }

    private void showWalletHistoryReport() {
        int walletId = reportView.readWalletIdReport(messages.get("report.wallet.history.prompt"));
        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            reportView.showErrorMessage(messages.get("transaction.wallet.notFound"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        List<Transaction> transactions = getTransactionsByWallet(walletId);
        if (transactions.isEmpty()) {
            reportView.showErrorMessage(messages.get("report.wallet.history.empty"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        List<Double> cashValues = new ArrayList<>();
        for (Transaction t : transactions) {
            Oracle quote = oracleDAO.findByDate(t.getOperationDate());
            double price = quote != null ? quote.getPrice() : 0.0;
            cashValues.add(t.getQuantity() * price);
        }

        reportView.showWalletHistoryReport(wallet, transactions, cashValues);
        ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
    }

    private void showWalletGainLossReport() {
        List<Wallet> wallets = walletDAO.findAll();
        if (wallets.isEmpty()) {
            reportView.showErrorMessage(messages.get("report.wallets.empty"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        List<Double> coinBalances = new ArrayList<>();
        List<Double> gainLosses = new ArrayList<>();
        Oracle currentQuote = oracleDAO.findByDate(new Date());
        double latestPrice = currentQuote != null ? currentQuote.getPrice() : 0.0;

        for (Wallet wallet : wallets) {
            List<Transaction> transactions = getTransactionsByWallet(wallet.getId());
            double bought = 0, sold = 0, spent = 0, received = 0;

            for (Transaction t : transactions) {
                Oracle q = oracleDAO.findByDate(t.getOperationDate());
                double price = q != null ? q.getPrice() : 0.0;
                double val = t.getQuantity() * price;

                if (t.getOperationType() == OperationType.CASH_IN) {
                    bought += t.getQuantity();
                    spent += val;
                } else {
                    sold += t.getQuantity();
                    received += val;
                }
            }

            double balance = bought - sold;
            double holdingsValue = balance * latestPrice;
            double gainLoss = (received + holdingsValue) - spent;

            coinBalances.add(balance);
            gainLosses.add(gainLoss);
        }

        reportView.showWalletGainLossReport(wallets, coinBalances, gainLosses);
        ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
    }

    private void showApurationResultsReport() {
        int walletId = reportView.readWalletIdReport();
        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            reportView.showErrorMessage(messages.get("transaction.wallet.notFound"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        List<Transaction> transactions = getTransactionsByWallet(walletId);
        if (transactions.isEmpty()) {
            reportView.showErrorMessage(messages.get("report.wallet.history.empty"));
            ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
            return;
        }

        List<Double> cotacoes = new ArrayList<>();
        List<Double> custosMedios = new ArrayList<>();
        List<Double> sdsFin = new ArrayList<>();
        List<Double> resOps = new ArrayList<>();
        List<Double> resAcums = new ArrayList<>();
        List<Double> sdsMerc = new ArrayList<>();

        double currentQtd = 0;
        double currentSdFin = 0;
        double currentAvgCost = 0;
        double totalVendas = 0;
        double custoTotalVendas = 0;
        double resAcumulado = 0;

        for (Transaction t : transactions) {
            Oracle quote = oracleDAO.findByDate(t.getOperationDate());
            double cotacao = quote != null ? quote.getPrice() : 0.0;
            cotacoes.add(cotacao);

            double resOp = 0;
            if (t.getOperationType() == OperationType.CASH_IN) {
                double buyVal = t.getQuantity() * cotacao;
                currentQtd += t.getQuantity();
                currentSdFin += buyVal;
                currentAvgCost = currentQtd > 0 ? currentSdFin / currentQtd : 0;
            } else {
                double sellVal = t.getQuantity() * cotacao;
                double costVal = t.getQuantity() * currentAvgCost;
                totalVendas += sellVal;
                custoTotalVendas += costVal;

                resOp = sellVal - costVal;
                resAcumulado += resOp;

                currentQtd -= t.getQuantity();
                currentSdFin -= costVal;
            }

            custosMedios.add(currentAvgCost);
            sdsFin.add(currentSdFin);
            resOps.add(resOp);
            resAcums.add(resAcumulado);
            sdsMerc.add(currentQtd * cotacao);
        }

        double resultadoOperacional = totalVendas - custoTotalVendas;
        Oracle latestQuoteObj = oracleDAO.findByDate(new Date());
        double cotacaoAtual = latestQuoteObj != null ? latestQuoteObj.getPrice() : cotacoes.get(cotacoes.size() - 1);
        double valorHoldings = currentQtd * currentAvgCost;
        double resultadoSimulado = resAcumulado + (currentQtd * cotacaoAtual - valorHoldings);

        reportView.showApurationResultsReport(
                transactions,
                cotacoes,
                custosMedios,
                sdsFin,
                resOps,
                resAcums,
                sdsMerc,
                totalVendas,
                custoTotalVendas,
                resultadoOperacional,
                currentQtd,
                currentAvgCost,
                valorHoldings,
                cotacaoAtual,
                resultadoSimulado
        );
        ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
    }

    private List<Transaction> getTransactionsByWallet(int walletId) {
        return transactionDAO.findByWalletId(walletId).stream()
                .filter(t -> t.getWalletId() == walletId)
                .collect(Collectors.toList());
    }
}
