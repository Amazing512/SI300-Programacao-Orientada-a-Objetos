package UnitTest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

class ReportControllerTest {

    @Test
    void calculatesFinancialReportWithHistoricalQuotes() {
        WalletMemoryDAO wallets = new WalletMemoryDAO();
        TransactionMemoryDAO transactions = new TransactionMemoryDAO();
        OracleMemoryDAO oracles = new OracleMemoryDAO();
        Wallet wallet = wallets.create(new Wallet("UnitTest-Report", "Broker"));
        LocalDate purchaseDate = LocalDate.of(2026, 4, 10);
        LocalDate saleDate = LocalDate.of(2026, 4, 11);
        transactions.create(new Transaction(wallet.getId(), purchaseDate, OperationType.CASH_IN, 10.0));
        transactions.create(new Transaction(wallet.getId(), saleDate, OperationType.CASH_OUT, 2.0));
        Oracle todayQuote = oracles.findByDate(LocalDate.now());
        if (todayQuote == null) {
            todayQuote = oracles.create(new Oracle(LocalDate.now(), 8.0));
        }
        oracles.create(new Oracle(purchaseDate, 5.0));
        OracleController oracleController = new OracleController(oracles);
        CapturingReportView view = new CapturingReportView(wallet.getId());

        new ReportController(wallets, transactions, oracleController, view, messages())
                .showFinancialReport();

        double todayPrice = todayQuote.getPrice();
        assertArrayEquals(new double[] {
            wallet.getId(), 10.0, 2.0, 8.0, 50.0,
            2.0 * todayPrice, 8.0 * todayPrice, 10.0 * todayPrice - 50.0
        }, view.report);
    }

    @Test
    void reportsMissingWalletWithoutReadingTransactions() {
        CapturingReportView view = new CapturingReportView(999999);
        ReportController controller = new ReportController(
                new WalletMemoryDAO(), new TransactionMemoryDAO(),
                new OracleController(new OracleMemoryDAO()), view, messages());

        controller.showFinancialReport();

        assertNotNull(view.error);
    }

    private static MessageProvider messages() {
        return new MessageProvider("messages", "pt", "BR");
    }

    private static final class CapturingReportView extends ReportView {
        private final int walletId;
        private double[] report;
        private String error;

        CapturingReportView(int walletId) {
            super(null);
            this.walletId = walletId;
        }

        @Override
        public int readWalletIdReport() {
            return walletId;
        }

        @Override
        public void showFinancialReport(int id, double bought, double sold, double balance,
                double spent, double received, double holdings, double gainLoss) {
            report = new double[] {id, bought, sold, balance, spent, received, holdings, gainLoss};
        }

        @Override
        public void showErrorMessage(String message) {
            error = message;
        }
    }
}