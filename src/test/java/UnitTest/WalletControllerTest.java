package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.controller.WalletController;
import org.unicamp.poo.dao.impl.memory.WalletMemoryDAO;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.WalletView;

class WalletControllerTest {

    @Test
    void addsAndEditsWallet() throws Exception {
        WalletMemoryDAO dao = new WalletMemoryDAO();
        TestWalletView view = new TestWalletView();
        view.walletToCreate = new Wallet("UnitTest-Create", "Broker");
        WalletController controller = new WalletController(dao, view, messages());

        invoke(controller, "actionAddWallet");
        Wallet created = dao.findAll().stream().filter(wallet -> wallet.getHolder().equals("UnitTest-Create")).findFirst().orElseThrow();
        view.walletId = created.getId();
        view.updatedWallet = new Wallet(created.getId(), "UnitTest-Edited", "New Broker");

        invoke(controller, "actionEditWallet");

        assertEquals("UnitTest-Edited", dao.findById(created.getId()).getHolder());
        assertEquals("New Broker", dao.findById(created.getId()).getBroker());
    }

    @Test
    void removesWalletOnlyAfterConfirmation() throws Exception {
        WalletMemoryDAO dao = new WalletMemoryDAO();
        Wallet wallet = dao.create(new Wallet("UnitTest-Remove", "Broker"));
        TestWalletView view = new TestWalletView();
        view.walletId = wallet.getId();
        view.confirmDeletion = true;

        invoke(new WalletController(dao, view, messages()), "actionRemoveWallet");

        assertNull(dao.findById(wallet.getId()));
    }

    @Test
    void keepsWalletWhenRemovalIsCancelled() throws Exception {
        WalletMemoryDAO dao = new WalletMemoryDAO();
        Wallet wallet = dao.create(new Wallet("UnitTest-Keep", "Broker"));
        TestWalletView view = new TestWalletView();
        view.walletId = wallet.getId();
        view.confirmDeletion = false;

        invoke(new WalletController(dao, view, messages()), "actionRemoveWallet");

        assertEquals(wallet, dao.findById(wallet.getId()));
    }

    private static MessageProvider messages() {
        return new MessageProvider("messages", "pt", "BR");
    }

    private static void invoke(Object target, String name) throws Exception {
        Method method = target.getClass().getDeclaredMethod(name);
        method.setAccessible(true);
        method.invoke(target);
    }

    private static final class TestWalletView extends WalletView {
        private Wallet walletToCreate;
        private Wallet updatedWallet;
        private int walletId;
        private boolean confirmDeletion;

        TestWalletView() { super(null); }

        @Override public Wallet readWalletData() { return walletToCreate; }
        @Override public int readWalletId() { return walletId; }
        @Override public Wallet readWalletUpdates(Wallet wallet) { return updatedWallet; }
        @Override public boolean confirmDeletion() { return confirmDeletion; }
        @Override public void showSuccessMessage(String message) { }
        @Override public void showErrorMessage(String message) { }
        @Override public void displayWallet(Wallet wallet) { }
    }
}