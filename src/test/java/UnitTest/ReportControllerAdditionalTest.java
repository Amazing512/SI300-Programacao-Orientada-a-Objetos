package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.controller.OracleController;
import org.unicamp.poo.controller.ReportController;
import org.unicamp.poo.dao.impl.memory.OracleMemoryDAO;
import org.unicamp.poo.dao.impl.memory.TransactionMemoryDAO;
import org.unicamp.poo.dao.impl.memory.WalletMemoryDAO;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.enums.OperationType;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.ReportView;

class ReportControllerAdditionalTest {

    @Test
    void ordersWalletsByIdAndByHolder() throws Exception {
        WalletMemoryDAO wallets = new WalletMemoryDAO();
        Wallet first = wallets.create(new Wallet("UnitTest-Order-Z", "Broker"));
        Wallet second = wallets.create(new Wallet("UnitTest-Order-A", "Broker"));
        CapturingReportView view = new CapturingReportView(0);
        ReportController controller = controller(wallets, new TransactionMemoryDAO(), new OracleMemoryDAO(), view);

        invoke(controller, "showWalletsOrderedById");
        assertTrue(indexOf(view.walletsById, first) < indexOf(view.walletsById, second));

        invoke(controller, "showWalletsOrderedByHolder");
        assertTrue(indexOf(view.walletsByHolder, second) < indexOf(view.walletsByHolder, first));
    }

    @Test
    void calculatesCurrentBalance() throws Exception {
        WalletMemoryDAO wallets = new WalletMemoryDAO();
        Wallet wallet = wallets.create(new Wallet("UnitTest-Balance", "Broker"));
        TransactionMemoryDAO transactions = new TransactionMemoryDAO();
        transactions.create(new Transaction(wallet.getId(), LocalDate.now(), OperationType.CASH_IN, 10.0));
        transactions.create(new Transaction(wallet.getId(), LocalDate.now(), OperationType.CASH_OUT, 3.5));
        CapturingReportView view = new CapturingReportView(wallet.getId());

        invoke(controller(wallets, transactions, new OracleMemoryDAO(), view), "showWalletCurrentBalance");

        assertEquals(6.5, view.balance);
    }

    @Test
    void buildsHistoryWithHistoricalCashValues() throws Exception {
        WalletMemoryDAO wallets = new WalletMemoryDAO();
        Wallet wallet = wallets.create(new Wallet("UnitTest-History", "Broker"));
        TransactionMemoryDAO transactions = new TransactionMemoryDAO();
        LocalDate date = LocalDate.of(2026, 6, 1);
        transactions.create(new Transaction(wallet.getId(), date, OperationType.CASH_IN, 4.0));
        OracleMemoryDAO oracles = new OracleMemoryDAO();
        if (oracles.findByDate(LocalDate.now()) == null) {
            oracles.create(new Oracle(LocalDate.now(), 8.0));
        }
        oracles.create(new Oracle(date, 5.0));
        CapturingReportView view = new CapturingReportView(wallet.getId());

        invoke(controller(wallets, transactions, oracles, view), "showWalletHistory");

        assertEquals(1, view.history.size());
        assertEquals(20.0, view.cashValues.get(0));
    }

    @Test
    void calculatesGainLossForEachWallet() throws Exception {
        WalletMemoryDAO wallets = new WalletMemoryDAO();
        Wallet profitable = wallets.create(new Wallet("UnitTest-Gain", "Broker"));
        Wallet neutral = wallets.create(new Wallet("UnitTest-Neutral", "Broker"));
        TransactionMemoryDAO transactions = new TransactionMemoryDAO();
        transactions.create(new Transaction(profitable.getId(), LocalDate.now(), OperationType.CASH_IN, 2.0));
        transactions.create(new Transaction(neutral.getId(), LocalDate.now(), OperationType.CASH_IN, 1.0));
        OracleMemoryDAO oracles = new OracleMemoryDAO();
        if (oracles.findByDate(LocalDate.now()) == null) {
            oracles.create(new Oracle(LocalDate.now(), 8.0));
        }
        CapturingReportView view = new CapturingReportView(0);

        invoke(controller(wallets, transactions, oracles, view), "showWalletGainOrLoss");

        int profitableIndex = indexOf(view.gainLossWallets, profitable);
        int neutralIndex = indexOf(view.gainLossWallets, neutral);
        assertEquals(2.0, view.coinBalances.get(profitableIndex));
        assertEquals(0.0, view.financialGainLosses.get(profitableIndex));
        assertEquals(1.0, view.coinBalances.get(neutralIndex));
    }

    private static ReportController controller(WalletMemoryDAO wallets, TransactionMemoryDAO transactions,
            OracleMemoryDAO oracles, CapturingReportView view) {
        return new ReportController(wallets, transactions, new OracleController(oracles), view,
                new MessageProvider("messages", "pt", "BR"));
    }

    private static void invoke(ReportController controller, String name) throws Exception {
        Method method = ReportController.class.getDeclaredMethod(name);
        method.setAccessible(true);
        method.invoke(controller);
    }

    private static int indexOf(List<Wallet> wallets, Wallet expected) {
        for (int index = 0; index < wallets.size(); index++) {
            if (wallets.get(index).getId().equals(expected.getId())) {
                return index;
            }
        }
        throw new AssertionError("Wallet not found in report");
    }

    private static final class CapturingReportView extends ReportView {
        private final int walletId;
        private List<Wallet> walletsById = List.of();
        private List<Wallet> walletsByHolder = List.of();
        private List<Wallet> gainLossWallets = List.of();
        private List<Transaction> history = List.of();
        private List<Double> cashValues = List.of();
        private List<Double> coinBalances = List.of();
        private List<Double> financialGainLosses = List.of();
        private double balance;

        CapturingReportView(int walletId) {
            super(null);
            this.walletId = walletId;
        }

        @Override public int readWalletIdReport(String prompt) { return walletId; }
        @Override public void showWalletsOrderedByIdReport(List<Wallet> wallets) { walletsById = List.copyOf(wallets); }
        @Override public void showWalletsOrderedByHolderReport(List<Wallet> wallets) { walletsByHolder = List.copyOf(wallets); }
        @Override public void showWalletCurrentBalanceReport(Wallet wallet, double value) { balance = value; }
        @Override public void showWalletHistoryReport(Wallet wallet, List<Transaction> transactions, List<Double> values) {
            history = List.copyOf(transactions);
            cashValues = List.copyOf(values);
        }
        @Override public void showWalletGainLossReport(List<Wallet> wallets, List<Double> balances, List<Double> gains) {
            gainLossWallets = List.copyOf(wallets);
            coinBalances = List.copyOf(balances);
            financialGainLosses = List.copyOf(gains);
        }
        @Override public void showErrorMessage(String message) { }
    }
}