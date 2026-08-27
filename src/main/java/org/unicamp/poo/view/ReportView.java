package org.unicamp.poo.view;

import java.text.SimpleDateFormat;
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
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public ReportView(MessageProvider messages) {
        this.messages = messages;
    }

    public int readWalletIdReport(){
        return readWalletIdReport(messages.get("report.wallet.id.prompt"));
    }

    public int readWalletIdReport(String prompt){
        return ConsoleScanner.readInt(prompt, messages.get("generic.confirmInvalid"));
    }

    // Imprime o relatório detalhado de apuração de resultados (formato de tabela completa + resumo)
    public void showApurationResultsReport(
            List<Transaction> transactions,
            List<Double> cotacoes,
            List<Double> custosMedios,
            List<Double> sdsFin,
            List<Double> resOps,
            List<Double> resAcums,
            List<Double> sdsMerc,
            double totalVendas,
            double custoTotalVendas,
            double resultadoOperacional,
            double moedasAtuais,
            double precoMedio,
            double valorHoldings,
            double cotacaoAtual,
            double resultadoSimulado
    ) {
        System.out.println(YELLOW + "\n------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("                                                    " + messages.get("report.apuracao.title"));
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------" + RESET);

        System.out.printf("%-10s | %-10s | %-10s | %-10s | %-10s | %-10s | %-12s | %-10s | %-10s | %-12s%n",
                messages.get("report.apuracao.table.date"),
                messages.get("report.apuracao.table.buyQtd"),
                messages.get("report.apuracao.table.sellQtd"),
                messages.get("report.apuracao.table.sdQtd"),
                messages.get("report.apuracao.table.quote"),
                messages.get("report.apuracao.table.avgCost"),
                messages.get("report.apuracao.table.sdFin"),
                messages.get("report.apuracao.table.resOp"),
                messages.get("report.apuracao.table.resAcum"),
                messages.get("report.apuracao.table.sdMerc"));
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");

        double sdQtd = 0;
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            boolean isBuy = t.getOperationType() == OperationType.CASH_IN;
            sdQtd += isBuy ? t.getQuantity() : -t.getQuantity();

            String buyQtdStr = isBuy ? String.format("%.2f", t.getQuantity()) : "";
            String sellQtdStr = !isBuy ? String.format("%.2f", t.getQuantity()) : "";

            System.out.printf("%-10s | %-10s | %-10s | %-10.2f | %-10.2f | %-10.2f | %-12.2f | %-10.2f | %-10.2f | %-12.2f%n",
                    DATE_FORMAT.format(t.getOperationDate()),
                    buyQtdStr,
                    sellQtdStr,
                    sdQtd,
                    cotacoes.get(i),
                    custosMedios.get(i),
                    sdsFin.get(i),
                    resOps.get(i),
                    resAcums.get(i),
                    sdsMerc.get(i));
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(YELLOW + messages.get("report.apuracao.summary.header") + RESET);
        System.out.printf(messages.get("report.apuracao.soldFor") + " R$ %.2f%n", totalVendas);
        System.out.printf(messages.get("report.apuracao.boughtFor") + " R$ %.2f%n", custoTotalVendas);

        if (resultadoOperacional >= 0) {
            System.out.printf(messages.get("report.apuracao.profitOp") + " " + GREEN + "R$ %.2f" + RESET + "%n", resultadoOperacional);
        } else {
            System.out.printf(messages.get("report.apuracao.lossOp") + " " + RED + "R$ %.2f" + RESET + "%n", resultadoOperacional);
        }

        System.out.println("-----------------------------------------------------------------");
        System.out.printf(messages.get("report.apuracao.holdings") + " R$ %.2f%n", moedasAtuais, precoMedio, valorHoldings);
        System.out.printf(messages.get("report.apuracao.simulation") + "%n", cotacaoAtual);
        System.out.print(messages.get("report.apuracao.simulatedResult") + " ");

        if (resultadoSimulado >= 0) {
            System.out.println(GREEN + String.format("R$ %.2f", resultadoSimulado) + RESET);
        } else {
            System.out.println(RED + String.format("R$ %.2f", resultadoSimulado) + RESET);
        }
        System.out.println("-----------------------------------------------------------------");
    }

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

    public void showErrorMessage(String s) {
        System.out.println(RED + s + RESET);
    }
}
