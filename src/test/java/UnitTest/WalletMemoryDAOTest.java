package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.unicamp.poo.dao.impl.memory.WalletMemoryDAO;
import org.unicamp.poo.model.Wallet;

class WalletMemoryDAOTest {

    private final WalletMemoryDAO dao = new WalletMemoryDAO();

    @AfterEach
    void removeCreatedWallets() {
        dao.findAll().stream()
                .filter(wallet -> wallet.getHolder().startsWith("UnitTest-"))
                .map(Wallet::getId)
                .forEach(dao::delete);
    }

    @Test
    void createsAndFindsWalletByGeneratedId() {
        Wallet wallet = dao.create(new Wallet("UnitTest-Alice", "Broker"));

        assertNotNull(wallet.getId());
        assertEquals(wallet, dao.findById(wallet.getId()));
    }

    @Test
    void findAllReturnsCopyAndOrdersByHolder() {
        Wallet zulu = dao.create(new Wallet("UnitTest-Zulu", "Broker"));
        Wallet alpha = dao.create(new Wallet("UnitTest-Alpha", "Broker"));

        List<Wallet> all = dao.findAll();
        all.clear();

        assertEquals(2, dao.findAll().stream()
                .filter(wallet -> wallet.getHolder().startsWith("UnitTest-"))
                .count());
        assertEquals(alpha, dao.findAllOrderByHolder().stream()
                .filter(wallet -> wallet.getHolder().startsWith("UnitTest-"))
                .findFirst().orElseThrow());
        assertEquals(zulu, dao.findAllOrderByHolder().stream()
                .filter(wallet -> wallet.getHolder().startsWith("UnitTest-"))
                .reduce((first, second) -> second).orElseThrow());
    }

    @Test
    void updatesAndDeletesWallet() {
        Wallet original = dao.create(new Wallet("UnitTest-Original", "Broker"));
        Wallet replacement = new Wallet(original.getId(), "UnitTest-Replacement", "New Broker");

        dao.update(replacement);
        assertEquals(replacement, dao.findById(original.getId()));

        dao.delete(original.getId());
        assertNull(dao.findById(original.getId()));
    }
}