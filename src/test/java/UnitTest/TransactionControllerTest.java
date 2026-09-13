package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.controller.OracleController;
import org.unicamp.poo.controller.TransactionController;
import org.unicamp.poo.dao.impl.memory.OracleMemoryDAO;
import org.unicamp.poo.dao.impl.memory.TransactionMemoryDAO;
import org.unicamp.poo.dao.impl.memory.WalletMemoryDAO;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.enums.OperationType;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.TransactionView;

class TransactionControllerTest {

    @Test
    void buysCoinsAfterConfirmation() throws Exception {
        WalletMemoryDAO wallets = new WalletMemoryDAO();
        Wallet wallet = wallets.create(new Wallet("UnitTest-Buy", "Broker"));
        TransactionMemoryDAO transactions = new TransactionMemoryDAO();
        TestTransactionView view = new TestTransactionView();
        view.transaction = new Transaction(wallet.getId(), LocalDate.of(2026, 5, 1), OperationType.CASH_IN, 2.5);
        view.rejectConfirmation = false;

        invoke(new TransactionController(transactions, wallets, oracleController(), view, messages()), "actionBuyCoin");

        assertEquals(1, transactions.findByWalletId(wallet.getId()).size());
        assertEquals(2.5, transactions.findByWalletId(wallet.getId()).get(0).getQuantity());
        assertTrue(view.success != null);
    }

    @Test
    void doesNotBuyCoinsWhenConfirmationIsRejected() throws Exception {
        WalletMemoryDAO wallets = new WalletMemoryDAO();
        Wallet wallet = wallets.create(new Wallet("UnitTest-Buy-Cancel", "Broker"));
        TransactionMemoryDAO transactions = new TransactionMemoryDAO();
        TestTransactionView view = new TestTransactionView();
        view.transaction = new Transaction(wallet.getId(), LocalDate.now(), OperationType.CASH_IN, 2.0);
        view.rejectConfirmation = true;

        invoke(new TransactionController(transactions, wallets, oracleController(), view, messages()), "actionBuyCoin");

        assertEquals(0, transactions.findByWalletId(wallet.getId()).size());
        assertTrue(view.error != null);
    }

    @Test
    void rejectsBuyForUnknownWallet() throws Exception {
        TransactionMemoryDAO transactions = new TransactionMemoryDAO();
        TestTransactionView view = new TestTransactionView();
        view.transaction = new Transaction(999999, LocalDate.now(), OperationType.CASH_IN, 2.0);

        invoke(new TransactionController(transactions, new WalletMemoryDAO(), oracleController(), view, messages()), "actionBuyCoin");

        assertEquals(0, transactions.findByWalletId(999999).size());
        assertTrue(view.error != null);
    }

    @Test
    void sellsCoinsOnlyWithinCurrentBalance() throws Exception {
        WalletMemoryDAO wallets = new WalletMemoryDAO();
        Wallet wallet = wallets.create(new Wallet("UnitTest-Sell", "Broker"));
        TransactionMemoryDAO transactions = new TransactionMemoryDAO();
        transactions.create(new Transaction(wallet.getId(), LocalDate.now(), OperationType.CASH_IN, 10.0));
        TestTransactionView view = new TestTransactionView();
        view.walletId = wallet.getId();
        view.quantity = 3.0;
        view.operationDate = LocalDate.of(2026, 5, 2);
        view.rejectConfirmation = false;

        invoke(new TransactionController(transactions, wallets, oracleController(), view, messages()), "actionSellCoin");

        assertEquals(2, transactions.findByWalletId(wallet.getId()).size());
        Transaction sale = transactions.findByWalletId(wallet.getId()).get(1);
        assertEquals(OperationType.CASH_OUT, sale.getOperationType());
        assertEquals(view.operationDate, sale.getOperationDate());
    }

    @Test
    void rejectsSaleAboveCurrentBalance() throws Exception {
        WalletMemoryDAO wallets = new WalletMemoryDAO();
        Wallet wallet = wallets.create(new Wallet("UnitTest-Sell-Limit", "Broker"));
        TransactionMemoryDAO transactions = new TransactionMemoryDAO();
        transactions.create(new Transaction(wallet.getId(), LocalDate.now(), OperationType.CASH_IN, 2.0));
        TestTransactionView view = new TestTransactionView();
        view.walletId = wallet.getId();
        view.quantity = 3.0;

        invoke(new TransactionController(transactions, wallets, oracleController(), view, messages()), "actionSellCoin");

        assertEquals(1, transactions.findByWalletId(wallet.getId()).size());
        assertTrue(view.error != null);
    }

    private static OracleController oracleController() {
        OracleMemoryDAO oracles = new OracleMemoryDAO();
        if (oracles.findByDate(LocalDate.now()) == null) {
            oracles.create(new Oracle(LocalDate.now(), 8.0));
        }
        return new OracleController(oracles);
    }

    private static MessageProvider messages() {
        return new MessageProvider("messages", "pt", "BR");
    }

    private static void invoke(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }

    private static final class TestTransactionView extends TransactionView {
        private Transaction transaction;
        private boolean rejectConfirmation;
        private int walletId;
        private double quantity;
        private LocalDate operationDate = LocalDate.now();
        private String error;
        private String success;

        TestTransactionView() { super(null); }

        @Override public Transaction readTransactionData(OperationType type) { return transaction; }
        @Override public boolean confirmRejectTransaction(String message) { return rejectConfirmation; }
        @Override public int readWalletId() { return walletId; }
        @Override public Double readQuantity() { return quantity; }
        @Override public LocalDate readOperationDate() { return operationDate; }
        @Override public void displayDailyQuote(Oracle quote) { }
        @Override public void displayWalletBalance(double balance) { }
        @Override public void showErrorMessage(String message) { error = message; }
        @Override public void showSuccessMessage(String message) { success = message; }
    }
}