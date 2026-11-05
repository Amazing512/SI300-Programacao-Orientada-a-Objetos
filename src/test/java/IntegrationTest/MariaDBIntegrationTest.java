package IntegrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.unicamp.poo.dao.impl.mariadb.OracleMariaDBDAO;
import org.unicamp.poo.dao.impl.mariadb.TransactionDAOImplMariaDB;
import org.unicamp.poo.dao.impl.mariadb.WalletDAOImplMariaDB;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.enums.OperationType;

@EnabledIfSystemProperty(named = "mariadb.integration", matches = "true")
class MariaDBIntegrationTest {

    private Connection connection;

    @BeforeEach
    void connectToTestDatabase() throws SQLException {
        String url = requiredEnvironmentVariable("MARIADB_TEST_URL");
        String user = requiredEnvironmentVariable("MARIADB_TEST_USER");
        String password = requiredEnvironmentVariable("MARIADB_TEST_PASSWORD");

        assertTrue(url.startsWith("jdbc:mariadb://"),
                "MARIADB_TEST_URL must point to a MariaDB test database");

        connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
    }

    @AfterEach
    void rollbackAndCloseConnection() throws SQLException {
        if (connection != null) {
            try {
                connection.rollback();
            } finally {
                connection.close();
            }
        }
    }

    @Test
    void persistsAndReadsWalletsAndSupportsCrudOperations() {
        WalletDAOImplMariaDB wallets = new WalletDAOImplMariaDB(connection);
        String token = UUID.randomUUID().toString().substring(0, 8);
        Wallet wallet = new Wallet("IT-" + token, "Broker-" + token);

        Wallet created = wallets.create(wallet);

        assertNotNull(created);
        assertNotNull(created.getId());
        Wallet persisted = wallets.findById(wallet.getId());
        assertNotNull(persisted);
        assertEquals(wallet.getId(), persisted.getId());
        assertEquals(wallet.getHolder(), persisted.getHolder());
        assertTrue(wallets.findAll().stream().anyMatch(found -> found.getId().equals(wallet.getId())));
        assertTrue(wallets.findAllOrderByHolder().stream()
                .anyMatch(found -> found.getId().equals(wallet.getId())));

        Wallet updated = new Wallet(wallet.getId(), "Updated-" + token, "UpdatedBroker-" + token);
        wallets.update(updated);

        Wallet persistedUpdate = wallets.findById(wallet.getId());
        assertNotNull(persistedUpdate);
        assertEquals(updated.getHolder(), persistedUpdate.getHolder());
        assertEquals(updated.getBroker(), persistedUpdate.getBroker());

        wallets.delete(wallet.getId());
        assertNull(wallets.findById(wallet.getId()));
    }

    @Test
    void persistsAndReadsTransactionsForAWallet() {
        Wallet wallet = new Wallet("IT-" + UUID.randomUUID().toString().substring(0, 8), "IT-Broker");
        assertNotNull(new WalletDAOImplMariaDB(connection).create(wallet));
        assertNotNull(wallet.getId());

        LocalDate date = LocalDate.now().minusDays(1);
        Transaction transaction = new Transaction(wallet.getId(), date, OperationType.CASH_IN, 3.75);
        TransactionDAOImplMariaDB transactions = new TransactionDAOImplMariaDB(connection);

        Transaction created = transactions.create(transaction);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertTrue(transactions.findByWalletId(wallet.getId()).stream().anyMatch(found ->
                found.getId().equals(transaction.getId())
                        && found.getOperationDate().equals(date)
                        && found.getOperationType() == OperationType.CASH_IN
                        && found.getQuantity() == 3.75));
    }

    @Test
    void persistsAndReadsOraclePriceByDate() {
        OracleMariaDBDAO oracles = new OracleMariaDBDAO(connection);
        LocalDate date = LocalDate.of(1900, 1, 1)
                .plusDays(Math.abs(UUID.randomUUID().getLeastSignificantBits() % 100_000));
        double price = 123.45;
        Oracle oracle = new Oracle(date, price);

        assertNotNull(oracles.create(oracle));

        Oracle persisted = oracles.findByDate(date);
        assertNotNull(persisted);
        assertEquals(date, persisted.getDate());
        assertEquals(price, persisted.getPrice());
    }

    private static String requiredEnvironmentVariable(String name) {
        String value = System.getenv(name);
        assertNotNull(value, name + " must be set to run MariaDB integration tests");
        assertFalse(value.isBlank(), name + " must not be blank");
        return value;
    }
}
