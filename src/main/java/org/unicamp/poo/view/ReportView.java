package org.unicamp.poo.view;

import java.text.SimpleDateFormat;
import java.util.List;

import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.enums.OperationType;
import static org.unicamp.poo.util.ConsoleColors.GREEN;
import static org.unicamp.poo.util.ConsoleColors.RED;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import static org.unicamp.poo.util.ConsoleColors.YELLOW;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

/* Classe View responsável por interagir com o usuário e formatar layouts de console
    para relatórios financeiros e de ranking. */

public class ReportView {

    private final MessageProvider messages;

    // Formato de data usado para entrada e saída do Oráculo
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

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

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            double cashValue = cashValues.get(i);
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

    // Imprime o total de ganho ou perda de cada carteira com saldo de moedas e valor real.
    public void showWalletGainLossReport(List<Wallet> wallets, List<Double> coinBalances, List<Double> financialGainLosses) {
        System.out.println(YELLOW + "\n-----------------------------------------------------------------");
        System.out.println(messages.get("report.wallet.gainLoss.title"));
        System.out.println("-----------------------------------------------------------------" + RESET);
        System.out.printf("%-10s | %-25s | %-15s | %-20s%n", 
                          messages.get("report.wallet.gainLoss.table.id"), 
                          messages.get("report.wallet.gainLoss.table.holder"), 
                          messages.get("report.wallet.gainLoss.table.coinBalance"),
                          messages.get("report.wallet.gainLoss.table.netProfit"));
        System.out.println("-----------------------------------------------------------------");

        for (int i = 0; i < wallets.size(); i++) {
            Wallet wallet = wallets.get(i);
            double coinBalance = coinBalances.get(i);
            double gainLoss = financialGainLosses.get(i);

            String gainLossStr = gainLoss < 0 ? 
                                 RED + String.format("R$ %.2f", gainLoss) + RESET : 
                                 GREEN + String.format("R$ %.2f", gainLoss) + RESET;

            System.out.printf("%-10d | %-25s | %-15.4f | %s%n", 
                              wallet.getId(), 
                              wallet.getHolder(), 
                              coinBalance, 
                              gainLossStr);
        }

        System.out.println("-----------------------------------------------------------------");
    }

    // Imprime uma mensagem de erro destacada na cor VERMELHA
    public void showErrorMessage(String s) {
        System.out.println(RED + s + RESET);
    }
}