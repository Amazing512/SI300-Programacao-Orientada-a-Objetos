package org.unicamp.poo.controller;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.TransactionFactory;
import org.unicamp.poo.model.enums.OperationType;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import static org.unicamp.poo.util.ConsoleColors.YELLOW;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.Menu;
import org.unicamp.poo.view.TransactionView;

public class TransactionController {
    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

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

    private double calculateWalletBalance (int walletId){
        List<Transaction> transactions = model.findByWalletId(walletId);
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

    private void actionBuyCoin() {
        Oracle dailyQuote = oracleController.getOrGenerateDailyQuote();
        if (dailyQuote == null) {
            log.warn("Não foi possível obter a cotação do oráculo para a compra.");
            view.showErrorMessage(messages.get("transaction.oracle.notAvailable"));
            return;
        }
        view.displayDailyQuote(dailyQuote);

        Transaction newTransaction = view.readTransactionData(OperationType.CASH_IN);

        if (newTransaction != null){
            MDC.put("walletId", String.valueOf(newTransaction.getWalletId()));
            try {
                if (newTransaction.getQuantity() <= 0){
                    log.warn("Tentativa de compra com valor menor ou igual a zero: {}", newTransaction.getQuantity());
                    view.showErrorMessage(messages.get("transaction.error.negativeValue"));
                    return;
                }

                var wallet = walletModel.findById(newTransaction.getWalletId());
                if (wallet == null) {
                    log.warn("Tentativa de compra para carteira inexistente ID {}", newTransaction.getWalletId());
                    view.showErrorMessage(messages.get("transaction.wallet.notFound"));
                    return;
                }

                double totalValue = newTransaction.getQuantity() * dailyQuote.getPrice();
                log.info("Processando compra: quantidade={}, cotação={}, total=R$ {}",
                        newTransaction.getQuantity(), dailyQuote.getPrice(), totalValue);

                String confirmMsg = messages.get("transaction.confirm.buy.part1") + " "
                                  + String.format("%.4f", newTransaction.getQuantity()) + " "
                                  + messages.get("transaction.confirm.buy.part2") + " "
                                  + String.format("%.2f", totalValue)
                                  + "?";

                if (view.confirmRejectTransaction(confirmMsg)) {
                    log.debug("Compra cancelada na confirmação pelo usuário.");
                    view.showErrorMessage(messages.get("transaction.cancelled"));
                    return;
                }

                Transaction savedTransaction = model.create(newTransaction);
                if (savedTransaction != null) {
                    log.info("Compra efetuada com sucesso: ID transação={}, carteira={}",
                            savedTransaction.getId(), savedTransaction.getWalletId());
                    view.showSuccessMessage(messages.get("transaction.buy.success"));
                }
                else {
                    log.error("Erro ao salvar transação de compra para a carteira ID {}", newTransaction.getWalletId());
                    view.showErrorMessage(messages.get("transaction.buy.error"));
                }
            } finally {
                MDC.clear();
            }
        } else {
            log.debug("Leitura de dados para compra cancelada pelo usuário.");
            view.showErrorMessage(messages.get("transaction.cancelled"));
        }
    }

    private void actionSellCoin() {
        Oracle dailyQuote = oracleController.getOrGenerateDailyQuote();
        if (dailyQuote == null) {
            log.warn("Não foi possível obter a cotação do oráculo para a venda.");
            view.showErrorMessage(messages.get("transaction.oracle.notAvailable"));
            return;
        }
        view.displayDailyQuote(dailyQuote);

        int walletId = view.readWalletId();
        MDC.put("walletId", String.valueOf(walletId));
        try {
            var wallet = walletModel.findById(walletId);
            if (wallet == null){
                log.warn("Tentativa de venda para carteira inexistente ID {}", walletId);
                view.showErrorMessage(messages.get("transaction.wallet.notFound"));
                return;
            }

            double currentBalance = calculateWalletBalance(walletId);
            view.displayWalletBalance(currentBalance);

            Double quantity = view.readQuantity();
            if (quantity == null) {
                log.debug("Venda cancelada durante a leitura da quantidade.");
                view.showErrorMessage(messages.get("transaction.cancelled"));
                return;
            }

            if (quantity > currentBalance) {
                log.warn("Tentativa de venda acima do saldo disponível na carteira ID {}: saldo atual={}, quantidade solicitada={}",
                        walletId, currentBalance, quantity);
                view.showErrorMessage(messages.get("transaction.sell.insufficientBalance"));
                return;
            }

            LocalDate operationDate = view.readOperationDate();
            Transaction newTransaction = TransactionFactory.createSell(walletId, operationDate, quantity);

            double totalValue = newTransaction.getQuantity() * dailyQuote.getPrice();
            log.info("Processando venda: quantidade={}, cotação={}, total=R$ {}",
                    newTransaction.getQuantity(), dailyQuote.getPrice(), totalValue);

            String confirmMsg = messages.get("transaction.confirm.sell.part1") + " "
                              + String.format("%.4f", newTransaction.getQuantity()) + " "
                              + messages.get("transaction.confirm.sell.part2") + " "
                              + String.format("%.2f", totalValue)
                              + "?";

            if (view.confirmRejectTransaction(confirmMsg)) {
                log.debug("Venda cancelada na confirmação pelo usuário.");
                view.showErrorMessage(messages.get("transaction.cancelled"));
                return;
            }

            Transaction savedTransaction = model.create(newTransaction);
            if (savedTransaction != null) {
                log.info("Venda efetuada com sucesso: ID transação={}, carteira={}",
                        savedTransaction.getId(), savedTransaction.getWalletId());
                view.showSuccessMessage(messages.get("transaction.sell.success"));
            }
            else {
                log.error("Erro ao salvar transação de venda para a carteira ID {}", walletId);
                view.showErrorMessage(messages.get("transaction.sell.error"));
            }
        } finally {
            MDC.clear();
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
