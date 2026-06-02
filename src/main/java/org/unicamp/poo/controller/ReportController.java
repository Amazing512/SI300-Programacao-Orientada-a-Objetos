package org.unicamp.poo.controller;

import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.enums.OperationType;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.Menu;
import org.unicamp.poo.view.ReportView;

import java.util.ArrayList;
import java.util.List;

public class ReportController {
    private final WalletDAO walletDAO;
    private final TransactionDAO transactionDAO;
    private final ReportView view;
    private final MessageProvider messages;

    public ReportController(WalletDAO walletDAO, TransactionDAO transactionDAO, ReportView view, MessageProvider messages) {
        super();
        this.walletDAO = walletDAO;
        this.transactionDAO = transactionDAO;
        this.view = view;
        this.messages = messages;
    }

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

    public void showFinancialReport(){
        int walletId = view.readWalletIdReport();

        Wallet wallet = walletDAO.findById(walletId);
        if (wallet == null){
            view.showErrorMessage(messages.get("transaction.wallet.notFound"));
        return;
        }

        List<Transaction> transactions = transactionDAO.findByWalletId(walletId);
        double totalCashIn = 0.0;
        double totalCashOut = 0.0;

        for(Transaction t : transactions){
            if (t.getOperationType() == OperationType.CASH_IN) {
                totalCashIn += t.getQuantity();
            }
            else if (t.getOperationType() == OperationType.CASH_OUT){
                totalCashOut += t.getQuantity();
            }
        }

        double result = totalCashIn - totalCashOut;

        view.showFinancialReport(walletId, totalCashIn, totalCashOut, result);
    }

    private void showSortedWallets(){
        List<Wallet> wallets = walletDAO.findAll();

        if(wallets == null || wallets.isEmpty()){
            view.showErrorMessage(messages.get("report.wallets.empty"));
            return;
        }

        wallets.sort ((w1, w2) -> {
            double balance1 = calculateWalletBalance(w1.getId());
            double balance2 = calculateWalletBalance(w2.getId());
            return Double.compare(balance2, balance1);
        });

        double[] sortedBalances = new double [wallets.size()];
        for (int i = 0; i < wallets.size(); i++){
            sortedBalances[i] = calculateWalletBalance(wallets.get(i).getId());
        }

        view.showSortedWalletsReport(wallets, sortedBalances);
    }

    private List<String> getMenuOptions(){
        final List<String> options = new ArrayList<>();
        options.add(messages.get("reportMenu.return"));
        options.add(messages.get("reportMenu.financial"));
        options.add(messages.get("reportMenu.sortedWallets"));
        return options;
    }
    public void start() {
        final List<String> options = getMenuOptions();
        final Menu reportMenu = new Menu(ConsoleScanner.getInstance());
        boolean loop = true;

        while (loop) {
            switch (reportMenu.getChoice(messages.get("reportMenu.title"), options, messages.get("reportMenu.prompt"))) {
                case 0 -> loop = false;
                case 1 -> showFinancialReport();
                case 2 -> showSortedWallets();
                default -> loop = false;
            }
        }
    }

}
