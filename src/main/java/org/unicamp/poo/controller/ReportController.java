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
import org.unicamp.poo.service.OracleService;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import static org.unicamp.poo.util.ConsoleColors.YELLOW;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.Menu;
import org.unicamp.poo.view.ReportView;

/**
 * controller responsável pela geração, cálculo e apresentação dos relatórios financeiros da aplicação.
 * Processa informações de posição consolidada, saldo atual, histórico de transações, listagens ordenadas
 * de carteiras e o demonstrativo contábil de ganho ou perda de capital com base no custo médio ponderado.
 */
public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final WalletDAO walletDAO;
    private final TransactionDAO transactionDAO;
    private final OracleService oracleService;
    private final ReportView view;
    private final MessageProvider messages;

    /**
     * Constrói o controller de relatórios injetando os DAOs, serviços e camada de visão.
     *
     * @param walletDAO      o DAO de acesso às carteiras
     * @param transactionDAO o DAO de acesso às transações
     * @param oracleService  o serviço de consulta ao Oráculo de cotações
     * @param view           a visão responsável pela impressão e renderização dos relatórios
     * @param messages       o provedor de mensagens internacionalizadas
     */
    public ReportController(
        WalletDAO walletDAO,
        TransactionDAO transactionDAO,
        OracleService oracleService,
        ReportView view,
        MessageProvider messages
    ) {
        super();
        this.walletDAO = walletDAO;
        this.transactionDAO = transactionDAO;
        this.oracleService = oracleService;
        this.view = view;
        this.messages = messages;
    }

    /**
     * Calcula o saldo em moedas de uma carteira específica somando aportes e subtraindo resgates.
     *
     * @param walletId o identificador da carteira
     * @return o saldo total de moedas
     */
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

    /**
     * Obtém a cotação do dia de hoje a partir do Oráculo, tratando falhas e retornando 0.0 se indisponível.
     *
     * @return o preço cotado de hoje ou 0.0
     */
    private double getTodayPrice() {
        try {
            Oracle todayQuote = oracleService.getDailyQuote();
            return (todayQuote != null) ? todayQuote.getPrice() : 0.0;
        } catch (Exception e) {
            log.warn("Não foi possível obter a cotação de hoje: {}", e.getMessage());
            return 0.0;
        }
    }

    private record LedgerRow(double price, double coinBalance, double averageCost, double operationResult, double accumulatedResult) {}

    /**
     * Monta o livro-razão contábil para as movimentações ordenadas cronologicamente,
     * apurando o preço praticado, saldo de moedas, custo médio e resultado de cada operação.
     *
     * @param sortedTransactions a lista de transações ordenadas cronologicamente
     * @return a lista de linhas contábeis calculadas
     */
    private List<LedgerRow> buildWalletLedger(List<Transaction> sortedTransactions) {
        double todayPrice = getTodayPrice();
        double coinBalance = 0.0;
        double averageCost = 0.0;
        double accumulatedResult = 0.0;
        List<LedgerRow> ledger = new ArrayList<>();

        for (Transaction transaction : sortedTransactions) {
            Oracle quote = oracleService.findByDate(transaction.getOperationDate());
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

    /**
     * Solicita o ID da carteira, calcula a posição consolidada (moedas compradas/vendidas,
     * saldo, valor investido, valor arrecadado, valor de custódia e resultado líquido) e exibe o relatório financeiro.
     */
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

        double todayPrice = getTodayPrice();

        for (Transaction transaction : transactions) {
            double price;
            Oracle quote = oracleService.findByDate(transaction.getOperationDate());

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

    /**
     * Recupera todas as carteiras e as exibe ordenadas crescentemente pelo ID.
     */
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

    /**
     * Recupera todas as carteiras e as exibe em ordem alfabética pelo nome do titular.
     */
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

    /**
     * Consulta e exibe o saldo acumulado de moedas para uma carteira selecionada.
     */
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

    /**
     * Consulta o histórico de movimentações da carteira e exibe com os valores financeiros calculados.
     */
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

        double todayPrice = getTodayPrice();

        for (Transaction transaction : transactions) {
            double price = todayPrice;
            Oracle quote = oracleService.findByDate(transaction.getOperationDate());
            if (quote != null) {
                price = quote.getPrice();
            }
            double value = transaction.getQuantity() * price;
            cashValues.add(value);
        }

        view.showWalletHistoryReport(wallet, transactions, cashValues);
    }

    /**
     * Apura o ganho ou perda de capital de cada operação pelo método do custo médio ponderado
     * e exibe o demonstrativo detalhado da carteira.
     */
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

    /**
     * Inicia o menu de relatórios financeiros, gerenciando a navegação e execução das opções do usuário.
     */
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
