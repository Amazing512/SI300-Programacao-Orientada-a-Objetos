package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.dao.impl.memory.TransactionMemoryDAO;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.enums.OperationType;

class TransactionMemoryDAOTest {

    @Test
    void createsTransactionWithGeneratedId() {
        Transaction transaction = new Transaction(71001, LocalDate.of(2026, 1, 10), OperationType.CASH_IN, 3.5);

        Transaction saved = new TransactionMemoryDAO().create(transaction);

        assertNotNull(saved.getId());
        assertEquals(transaction, saved);
    }

    @Test
    void findsOnlyTransactionsForRequestedWallet() {
        TransactionMemoryDAO dao = new TransactionMemoryDAO();
        dao.create(new Transaction(71002, LocalDate.of(2026, 1, 11), OperationType.CASH_IN, 4.0));
        dao.create(new Transaction(71002, LocalDate.of(2026, 1, 12), OperationType.CASH_OUT, 1.5));
        dao.create(new Transaction(71003, LocalDate.of(2026, 1, 13), OperationType.CASH_IN, 8.0));

        assertEquals(2, dao.findByWalletId(71002).size());
        assertEquals(0, dao.findByWalletId(999999).size());
    }
}