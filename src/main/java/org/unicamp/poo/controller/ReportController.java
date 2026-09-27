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

/*  
 * Controller responsável por gerar relatórios financeiros consolidados e ordenar listas de carteiras com base no seu saldo atual. 
 */
public class ReportController {

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

    // Calcula dinamicamente o saldo atual total de uma carteira específica.
    private double calculateWalletBalance (int walletId){
        List<Transaction> transactions = transactionDAO.findByWalletId(walletId);
        double balance = 0.0;

        for (Transaction transaction : transactions){
            if (transaction.getOperationType() == OperationType.CASH_IN) {
                balance += transaction.getQuantity(); // Incrementa o saldo na compra
            }
            else if (transaction.getOperationType() == OperationType.CASH_OUT){
                balance -= transaction.getQuantity(); // Decrementa o saldo na venda
            }
        }
        return balance;
    }

    // Retorna a cotação do dia atual (gera uma nova caso ainda não exista).
    private double getTodayPrice() {
        Oracle todayQuote = oracleController.getOrGenerateDailyQuote();
        return (todayQuote != null) ? todayQuote.getPrice() : 0.0;
    }

    // Uma linha do extrato: o estado da carteira logo após processar uma transação,
    // apurado pelo método do custo médio (replica as colunas Custo Médio, Res. Oper.
    // e Res. Acum. da planilha de referência).
    private record LedgerRow(double price, double coinBalance, double averageCost, double operationResult, double accumulatedResult) {}

    // Monta o extrato completo de uma carteira, transação a transação, na ordem informada
    // (que já deve estar em ordem cronológica): o custo médio só é recalculado numa compra
    // -- ponderando o custo médio anterior pelo saldo anterior com o valor da nova compra --
    // e uma venda apura resultado como (quantidade vendida) * (preço de venda - custo médio
    // vigente), sem alterar o custo médio.
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

    // Processa o fluxo de dados do relatório financeiro para uma carteira específica.
    public void showFinancialReport(){
        int walletId = view.readWalletIdReport();

        // Garante que a carteira existe antes de buscar as transações
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

        // Valor atual das moedas restantes (coinBalance * preço de hoje)
        double currentHoldingsValue = coinBalance * todayPrice;

        // Lucro / Prejuízo Financeiro = (Valor Atual das Moedas Restantes + Dinheiro Recebido de Vendas) - Dinheiro Gasto em Compras
        double totalFinancialGainLoss = (currentHoldingsValue + totalMoneyReceived) - totalMoneySpent;

        // Delega a apresentação visual para a camada view
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

    // Lista as carteiras ordenadas pelo identificador em ordem crescente.
    private void showWalletsOrderedById() {
        List<Wallet> wallets = walletDAO.findAll();

        if (wallets == null || wallets.isEmpty()) {
            view.showErrorMessage(messages.get("report.wallets.empty"));
            return;
        }

        wallets.sort(Comparator.comparingInt(Wallet::getId));
        view.showWalletsOrderedByIdReport(wallets);
    }

    // Lista as carteiras ordenadas alfabeticamente pelo nome do titular.
    private void showWalletsOrderedByHolder() {
        List<Wallet> wallets = walletDAO.findAllOrderByHolder();

        if (wallets == null || wallets.isEmpty()) {
            view.showErrorMessage(messages.get("report.wallets.empty"));
            return;
        }

        view.showWalletsOrderedByHolderReport(wallets);
    }

    // Exibe o saldo atual de uma única carteira.
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

    // Exibe o histórico de transações de uma única carteira.
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

    // Exibe o extrato de uma carteira: cada transação com custo médio e resultado
    // apurados pelo método do custo médio, mais um resumo final.
    private void showWalletGainOrLoss() {
        int walletId = view.readWalletIdReport(messages.get("report.wallet.history.prompt"));
        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            view.showErrorMessage(messages.get("transaction.wallet.notFound"));
            return;
        }

        List<Transaction> transactions = new ArrayList<>(transactionDAO.findByWalletId(walletId));
        transactions.sort(Comparator.comparing(Transaction::getOperationDate).thenComparing(Transaction::getId));

        if (transactions.isEmpty()) {
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

    // Gera a lista de opções de menu traduzidas para a seção de relatórios.
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

    // Inicia o loop de navegação do submenu principal de relatórios.
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
