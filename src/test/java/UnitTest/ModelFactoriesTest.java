package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.OraclePriceFactory;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.TransactionFactory;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.WalletFactory;
import org.unicamp.poo.model.enums.OperationType;

class ModelFactoriesTest {

    @Test
    void createsBuyTransaction() {
        LocalDate date = LocalDate.of(2026, 1, 15);
        Transaction tx = TransactionFactory.createBuy(1, date, 10.5);

        assertNotNull(tx);
        assertNull(tx.getId());
        assertEquals(1, tx.getWalletId());
        assertEquals(date, tx.getOperationDate());
        assertEquals(OperationType.CASH_IN, tx.getOperationType());
        assertEquals(10.5, tx.getQuantity());
    }

    @Test
    void createsSellTransaction() {
        LocalDate date = LocalDate.of(2026, 2, 20);
        Transaction tx = TransactionFactory.createSell(2, date, 4.0);

        assertNotNull(tx);
        assertNull(tx.getId());
        assertEquals(2, tx.getWalletId());
        assertEquals(date, tx.getOperationDate());
        assertEquals(OperationType.CASH_OUT, tx.getOperationType());
        assertEquals(4.0, tx.getQuantity());
    }

    @Test
    void reconstitutesTransactionFromDatabase() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        Transaction tx = TransactionFactory.fromDatabase(99, 3, date, OperationType.CASH_IN, 8.0);

        assertNotNull(tx);
        assertEquals(99, tx.getId());
        assertEquals(3, tx.getWalletId());
        assertEquals(OperationType.CASH_IN, tx.getOperationType());
    }

    @Test
    void validatesTransactionInput() {
        LocalDate date = LocalDate.now();

        assertThrows(IllegalArgumentException.class, () ->
            TransactionFactory.createBuy(1, date, 0.0)
        );
        assertThrows(IllegalArgumentException.class, () ->
            TransactionFactory.createBuy(1, date, -5.0)
        );
        assertThrows(IllegalArgumentException.class, () ->
            TransactionFactory.create(1, null, OperationType.CASH_IN, 1.0)
        );
        assertThrows(IllegalArgumentException.class, () ->
            TransactionFactory.create(1, date, null, 1.0)
        );
    }

    @Test
    void createsAndTrimsWallet() {
        Wallet wallet = WalletFactory.create("  Alice Silva  ", "  Binance  ");

        assertNotNull(wallet);
        assertNull(wallet.getId());
        assertEquals("Alice Silva", wallet.getHolder());
        assertEquals("Binance", wallet.getBroker());
    }

    @Test
    void createsWalletFromDatabase() {
        Wallet wallet = WalletFactory.fromDatabase(42, "Bob Santos", "Coinbase");

        assertNotNull(wallet);
        assertEquals(42, wallet.getId());
        assertEquals("Bob Santos", wallet.getHolder());
        assertEquals("Coinbase", wallet.getBroker());
    }

    @Test
    void validatesWalletInput() {
        assertThrows(IllegalArgumentException.class, () ->
            WalletFactory.create("", "Binance")
        );
        assertThrows(IllegalArgumentException.class, () ->
            WalletFactory.create("Alice", "   ")
        );
        assertThrows(IllegalArgumentException.class, () ->
            WalletFactory.create(null, "Binance")
        );
    }

    @Test
    void createsOracleQuote() {
        LocalDate date = LocalDate.of(2026, 5, 1);
        Oracle quote = OraclePriceFactory.create(date, 5.50);

        assertNotNull(quote);
        assertEquals(date, quote.getDate());
        assertEquals(5.50, quote.getPrice());
    }

    @Test
    void createsOracleQuoteForToday() {
        Oracle quote = OraclePriceFactory.createForToday(7.20);

        assertNotNull(quote);
        assertEquals(LocalDate.now(), quote.getDate());
        assertEquals(7.20, quote.getPrice());
    }

    @Test
    void validatesOracleInput() {
        assertThrows(IllegalArgumentException.class, () ->
            OraclePriceFactory.create(null, 5.0)
        );
        assertThrows(IllegalArgumentException.class, () ->
            OraclePriceFactory.create(LocalDate.now(), 0.0)
        );
        assertThrows(IllegalArgumentException.class, () ->
            OraclePriceFactory.create(LocalDate.now(), -1.0)
        );
    }

    public static void main(String[] args) {
        ModelFactoriesTest test = new ModelFactoriesTest();
        test.createsBuyTransaction();
        test.createsSellTransaction();
        test.reconstitutesTransactionFromDatabase();
        test.validatesTransactionInput();
        test.createsAndTrimsWallet();
        test.createsWalletFromDatabase();
        test.validatesWalletInput();
        test.createsOracleQuote();
        test.createsOracleQuoteForToday();
        test.validatesOracleInput();
        System.out.println("ModelFactories tests PASSED successfully!");
    }
}
