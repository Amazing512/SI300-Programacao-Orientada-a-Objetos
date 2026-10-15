package org.unicamp.poo.view;

import java.awt.HeadlessException;
import java.awt.print.PrinterException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.enums.OperationType;
import static org.unicamp.poo.util.ConsoleColors.GREEN;
import static org.unicamp.poo.util.ConsoleColors.RED;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import static org.unicamp.poo.util.ConsoleColors.YELLOW;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

/* View responsável por interagir com o usuário e formatar layouts para relatórios financeiros. */
public class ReportView {

    private final MessageProvider messages;

    // Formato de data usado para entrada e saída do Oráculo
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Construtor que fornece Injeção de Dependência para mensagens internacionalizadas
    public ReportView(MessageProvider messages) {
        this.messages = messages;
    }

    public int readWalletIdReport(){
        return readWalletIdReport(messages.get("report.wallet.id.prompt"));
    }

    public int readWalletIdReport(String prompt){
        return ConsoleScanner.readInt(prompt, messages.get("generic.confirmInvalid"));
    }

    // Renderiza um bloco de painel financeiro mostrando métricas e valores líquidos.
    public void showFinancialReport(
        int walletId,
        double totalCoinsBought,
        double totalCoinsSold,
        double coinBalance,
        double totalMoneySpent,
        double totalMoneyReceived,
        double currentHoldingsValue,
        double totalFinancialGainLoss
    ) {
        renderFinancialReport(walletId, totalCoinsBought, totalCoinsSold, coinBalance, totalMoneySpent, totalMoneyReceived, currentHoldingsValue, totalFinancialGainLoss);
        offerPrint(messages.get("report.financial.title").trim(), false, () -> renderFinancialReport(walletId, totalCoinsBought, totalCoinsSold, coinBalance, totalMoneySpent, totalMoneyReceived, currentHoldingsValue, totalFinancialGainLoss));
    }

    private void renderFinancialReport(
        int walletId,
        double totalCoinsBought,
        double totalCoinsSold,
        double coinBalance,
        double totalMoneySpent,
        double totalMoneyReceived,
        double currentHoldingsValue,
        double totalFinancialGainLoss
    ) {
        System.out.println(YELLOW + "\n----------------------------------");
        System.out.println(messages.get("report.financial.title"));
        System.out.println("----------------------------------"  + RESET);
        System.out.println(messages.get("report.financial.walletId") + " " +  walletId);
        System.out.printf(messages.get("report.financial.totalCashIn") + " " +  "%.4f%n", totalCoinsBought);
        System.out.printf(messages.get("report.financial.totalCashOut") + " " + "%.4f%n", totalCoinsSold);
        System.out.printf(messages.get("report.financial.result") + " " + "%.4f%n", coinBalance);
        System.out.println("----------------------------------");
        System.out.printf(messages.get("report.financial.moneySpent") + " R$ " + "%.2f%n", totalMoneySpent);
        System.out.printf(messages.get("report.financial.moneyReceived") + " R$ " + "%.2f%n", totalMoneyReceived);
        System.out.printf(messages.get("report.financial.holdingsValue") + " R$ " + "%.2f%n", currentHoldingsValue);
        System.out.println("----------------------------------");
        System.out.print(messages.get("report.financial.gainLoss") + " ");

        if (totalFinancialGainLoss < 0) {
            System.out.println(RED + String.format("R$ %.2f", totalFinancialGainLoss) + RESET);
        } else {
            System.out.println(GREEN + String.format("R$ %.2f", totalFinancialGainLoss) + RESET);
        }
        System.out.println("----------------------------------");
    }

    // Imprime carteiras ordenadas pelo identificador.
    public void showWalletsOrderedByIdReport(List<Wallet> wallets) {
        renderWalletsOrderedByIdReport(wallets);
        offerPrint(messages.get("report.wallets.byId.title").trim(), false, () -> renderWalletsOrderedByIdReport(wallets));
    }

    private void renderWalletsOrderedByIdReport(List<Wallet> wallets) {
        System.out.println(YELLOW + "\n---------------------------------");
        System.out.println(messages.get("report.wallets.byId.title"));
        System.out.println("---------------------------------" + RESET);

        System.out.printf("%-10s | %-25s | %-20s%n", messages.get("report.wallet.table.id"), messages.get("report.wallet.table.holder"), messages.get("report.wallet.table.broker"));
        System.out.println("---------------------------------");

        for (Wallet wallet : wallets) {
            System.out.printf("%-10d | %-25s | %-20s%n", wallet.getId(), wallet.getHolder(), wallet.getBroker());
        }

        System.out.println("---------------------------------");
    }

    // Imprime carteiras ordenadas alfabeticamente pelo nome do titular.
    public void showWalletsOrderedByHolderReport(List<Wallet> wallets) {
        renderWalletsOrderedByHolderReport(wallets);
        offerPrint(messages.get("report.wallets.byHolder.title").trim(), false, () -> renderWalletsOrderedByHolderReport(wallets));
    }

    private void renderWalletsOrderedByHolderReport(List<Wallet> wallets) {
        System.out.println(YELLOW + "\n---------------------------------");
        System.out.println(messages.get("report.wallets.byHolder.title"));
        System.out.println("---------------------------------" + RESET);

        System.out.printf("%-10s | %-25s | %-20s%n", messages.get("report.wallet.table.id"), messages.get("report.wallet.table.holder"), messages.get("report.wallet.table.broker"));
        System.out.println("---------------------------------");

        for (Wallet wallet : wallets) {
            System.out.printf("%-10d | %-25s | %-20s%n", wallet.getId(), wallet.getHolder(), wallet.getBroker());
        }

        System.out.println("---------------------------------");
    }

    // Imprime o saldo atual de uma única carteira.
    public void showWalletCurrentBalanceReport(Wallet wallet, double balance) {
        renderWalletCurrentBalanceReport(wallet, balance);
        offerPrint(messages.get("report.wallet.currentBalance.title").trim(), false, () -> renderWalletCurrentBalanceReport(wallet, balance));
    }

    private void renderWalletCurrentBalanceReport(Wallet wallet, double balance) {
        System.out.println(YELLOW + "\n---------------------------------");
        System.out.println(messages.get("report.wallet.currentBalance.title"));
        System.out.println("---------------------------------" + RESET);
        System.out.println(messages.get("report.wallet.currentBalance.id") + " " +  wallet.getId());
        System.out.println(messages.get("report.wallet.currentBalance.holder") + " " + wallet.getHolder());
        System.out.println(messages.get("report.wallet.currentBalance.broker") + " " + wallet.getBroker());
        System.out.printf(messages.get("report.wallet.currentBalance.balance") + " " + "%.2f%n", balance);
        System.out.println("---------------------------------");
    }

    // Imprime o histórico de transações de uma única carteira.
    public void showWalletHistoryReport(Wallet wallet, List<Transaction> transactions, List<Double> cashValues) {
        renderWalletHistoryReport(wallet, transactions, cashValues);
        offerPrint(messages.get("report.wallet.history.title").trim(), false, () -> renderWalletHistoryReport(wallet, transactions, cashValues));
    }

    private void renderWalletHistoryReport(Wallet wallet, List<Transaction> transactions, List<Double> cashValues) {
        System.out.println(YELLOW + "\n---------------------------------");
        System.out.println(messages.get("report.wallet.history.title"));
        System.out.println("---------------------------------" + RESET);
        System.out.println(messages.get("report.wallet.history.id") + " " + wallet.getId());
        System.out.println(messages.get("report.wallet.history.holder") + " " + wallet.getHolder());
        System.out.println(messages.get("report.wallet.history.broker") + " " + wallet.getBroker());
        System.out.println("---------------------------------");
        System.out.printf("%-12s | %-10s | %-10s | %-20s%n", 
                          messages.get("report.wallet.history.table.date"), 
                          messages.get("report.wallet.history.table.type"), 
                          messages.get("report.wallet.history.table.quantity"),
                          messages.get("report.wallet.history.table.value"));
        System.out.println("---------------------------------");

        for (int transactionIndex = 0; transactionIndex < transactions.size(); transactionIndex++) {
            Transaction transaction = transactions.get(transactionIndex);
            double cashValue = cashValues.get(transactionIndex);
            String typeLabel = transaction.getOperationType() == OperationType.CASH_IN ? messages.get("report.wallet.history.type.buy") : messages.get("report.wallet.history.type.sell");
            
            String valStr = transaction.getOperationType() == OperationType.CASH_IN ?
                    RED + String.format("- R$ %.2f", cashValue) + RESET :
                    GREEN + String.format("+ R$ %.2f", cashValue) + RESET;

            System.out.printf("%-12s | %-10s | %-10.2f | %s%n", 
                              DATE_FORMAT.format(transaction.getOperationDate()), 
                              typeLabel, 
                              transaction.getQuantity(),
                              valStr);
        }

        System.out.println("---------------------------------");
    }

    // Imprime o extrato de uma carteira: cada transação com preço, saldo, custo médio,
    // resultado da operação e resultado acumulado, apurados pelo método do custo médio.
    public void showWalletGainLossReport(
        Wallet wallet,
        List<Transaction> transactions,
        List<Double> prices,
        List<Double> coinBalances,
        List<Double> averageCosts,
        List<Double> operationResults,
        List<Double> accumulatedResults
    ) {
        renderWalletGainLossReport(wallet, transactions, prices, coinBalances, averageCosts, operationResults, accumulatedResults);
        offerPrint(messages.get("report.wallet.gainLoss.title").trim(), true, () -> renderWalletGainLossReport(wallet, transactions, prices, coinBalances, averageCosts, operationResults, accumulatedResults));
    }

    private void renderWalletGainLossReport(
        Wallet wallet,
        List<Transaction> transactions,
        List<Double> prices,
        List<Double> coinBalances,
        List<Double> averageCosts,
        List<Double> operationResults,
        List<Double> accumulatedResults
    ) {
        String separator = "------------------------------------------------------------------------------------------------------------------";

        System.out.println(YELLOW + "\n" + separator);
        System.out.println(messages.get("report.wallet.gainLoss.title"));
        System.out.println(separator + RESET);
        System.out.println(messages.get("report.wallet.history.id") + " " + wallet.getId());
        System.out.println(messages.get("report.wallet.history.holder") + " " + wallet.getHolder());
        System.out.println(messages.get("report.wallet.history.broker") + " " + wallet.getBroker());
        System.out.println(separator);

        System.out.printf("%-12s | %-8s | %-10s | %-10s | %-10s | %-14s | %-18s | %-18s%n",
                          messages.get("report.wallet.history.table.date"),
                          messages.get("report.wallet.history.table.type"),
                          messages.get("report.wallet.history.table.quantity"),
                          messages.get("report.wallet.gainLoss.table.price"),
                          messages.get("report.wallet.gainLoss.table.balance"),
                          messages.get("report.wallet.gainLoss.table.averageCost"),
                          messages.get("report.wallet.gainLoss.table.operationResult"),
                          messages.get("report.wallet.gainLoss.table.accumulatedResult"));
        System.out.println(separator);

        for (int index = 0; index < transactions.size(); index++) {
    Transaction transaction = transactions.get(index);
    String typeLabel = transaction.getOperationType() == OperationType.CASH_IN
        ? messages.get("report.wallet.history.type.buy")
        : messages.get("report.wallet.history.type.sell");

    double operationResult = operationResults.get(index);
    double accumulatedResult = accumulatedResults.get(index);

    // 1. Formata apenas os valores como texto puro primeiro (sem cores)
    String rawOperation = String.format("R$ %.2f", operationResult);
    String rawAccumulated = String.format("R$ %.2f", accumulatedResult);

    // 2. Aplica o espaçamento (padding) exato exigido pelo cabeçalho (18 caracteres)
    String paddedOperation = String.format("%-18s", rawOperation);
    String paddedAccumulated = String.format("%-18s", rawAccumulated);

    // 3. Aplica as cores no texto que já está perfeitamente espaçado
    String operationStr = operationResult < 0 
        ? RED + paddedOperation + RESET 
        : GREEN + paddedOperation + RESET;
    String accumulatedStr = accumulatedResult < 0 
        ? RED + paddedAccumulated + RESET 
        : GREEN + paddedAccumulated + RESET;

    // 4. Imprime usando a correção da data e inserindo as strings já coloridas e espaçadas
    System.out.printf("%-12s | %-8s | %-10.4f | %-10.4f | %-10.4f | %-14s | %s | %s%n",
                      transaction.getOperationDate().format(DATE_FORMAT), // <-- Correção do erro de Data
                      typeLabel,
                      transaction.getQuantity(),
                      prices.get(index),
                      coinBalances.get(index),
                      String.format("R$ %.4f", averageCosts.get(index)),
                      operationStr,
                      accumulatedStr);
}
        System.out.println(separator);

        double finalResult = accumulatedResults.get(accumulatedResults.size() - 1);
        System.out.print(messages.get("report.financial.gainLoss") + " ");
        if (finalResult < 0) {
            System.out.println(RED + String.format("R$ %.2f", finalResult) + RESET);
        } else {
            System.out.println(GREEN + String.format("R$ %.2f", finalResult) + RESET);
        }
        System.out.println(separator);
    }

    // Pergunta se o usuário quer imprimir o relatório que acabou de aparecer na tela.
    // "render" é a receita que desenha o relatório: ela já existe (é o que mostra na tela),
    // então a reaproveitamos para gerar as linhas que vão para o papel.
    private void offerPrint(String jobName, boolean landscape, Runnable render) {
        if (!askToPrint()) {
            return;
        }

        List<String> lines = captureLines(render);

        try {
            boolean printed = new ReportPrinter(jobName, lines, landscape).print();
            System.out.println(printed ? messages.get("report.print.sent") : messages.get("report.print.cancelled"));
        } catch (PrinterException | HeadlessException exception) {
            String detail = (exception.getMessage() == null) ? "" : " " + exception.getMessage();
            showErrorMessage(messages.get("report.print.error") + detail);
        }
    }

    // Pergunta "Deseja imprimir?" com as opções 1 (Sim) e 2 (Não), igual aos outros menus do projeto.
    private boolean askToPrint() {
        while (true) {
            System.out.println(messages.get("report.print.prompt"));
            System.out.println("1 - " + messages.get("generic.confirmYes"));
            System.out.println("2 - " + messages.get("generic.confirmNo"));

            Integer option = ConsoleScanner.readIntOrNull();
            if (option != null && option == 1) {
                return true;
            }
            if (option != null && option == 2) {
                return false;
            }
            System.out.println(messages.get("generic.confirmInvalid"));
        }
    }

    // Executa o relatório "em silêncio": em vez de aparecer na tela, o texto é guardado na memória.
    // Depois tira as cores (que só servem para o console) e devolve o texto linha por linha.
    private List<String> captureLines(Runnable render) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            render.run();
        } finally {
            System.setOut(originalOut); // sempre devolve a tela ao normal, mesmo se der erro
        }

        String text = buffer.toString(StandardCharsets.UTF_8).replaceAll("\\u001B\\[[0-9;]*m", "");
        List<String> lines = new ArrayList<>(text.lines().toList());

        // Remove linhas em branco do começo e do fim
        while (!lines.isEmpty() && lines.get(0).isBlank()) {
            lines.remove(0);
        }
        while (!lines.isEmpty() && lines.get(lines.size() - 1).isBlank()) {
            lines.remove(lines.size() - 1);
        }

        return lines;
    }

    // Imprime uma mensagem de erro destacada na cor VERMELHA
    public void showErrorMessage(String message) {
        System.out.println(RED + message + RESET);
    }
}