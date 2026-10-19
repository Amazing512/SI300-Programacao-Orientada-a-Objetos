package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.dao.impl.mariadb.OracleMariaDBDAO;
import org.unicamp.poo.dao.impl.mariadb.TransactionDAOImplMariaDB;
import org.unicamp.poo.dao.impl.mariadb.WalletDAOImplMariaDB;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.enums.OperationType;

class MariaDBDaoTest {

    @Test
    void createsWalletAndAssignsGeneratedId() {
        JdbcFixture jdbc = new JdbcFixture(List.of(), 42, false);
        Wallet wallet = new Wallet("Alice", "Broker");

        Wallet result = new WalletDAOImplMariaDB(jdbc.connection).create(wallet);

        assertSame(wallet, result);
        assertEquals(42, wallet.getId());
        assertEquals("Alice", jdbc.parameters.get(1));
        assertEquals("Broker", jdbc.parameters.get(2));
        assertTrue(jdbc.sql.startsWith("INSERT INTO CARTEIRA"));
    }

    @Test
    void findsWalletByIdAndMapsItsColumns() {
        JdbcFixture jdbc = new JdbcFixture(List.of(Map.of(
                "IdCarteira", 7, "Titular", "Beatriz", "Corretora", "Exchange")), null, false);

        Wallet wallet = new WalletDAOImplMariaDB(jdbc.connection).findById(7);

        assertEquals(7, wallet.getId());
        assertEquals("Beatriz", wallet.getHolder());
        assertEquals("Exchange", wallet.getBroker());
        assertEquals(7, jdbc.parameters.get(1));
    }

    @Test
    void returnsEmptyWalletListWhenQueryFails() {
        JdbcFixture jdbc = new JdbcFixture(List.of(), null, true);

        assertTrue(new WalletDAOImplMariaDB(jdbc.connection).findAll().isEmpty());
    }

    @Test
    void findsTransactionsAndSkipsRowsWithUnknownOperationCodes() {
        JdbcFixture jdbc = new JdbcFixture(List.of(
                Map.of("IdCarteira", 12, "Data", Date.valueOf(LocalDate.of(2026, 10, 1)),
                        "TipoOperacao", "C", "Quantidade", 3.5, "IdMovimento", 91),
                Map.of("IdCarteira", 12, "Data", Date.valueOf(LocalDate.of(2026, 10, 1)),
                        "TipoOperacao", "X", "Quantidade", 1.0, "IdMovimento", 92)), null, false);

        List<Transaction> transactions = new TransactionDAOImplMariaDB(jdbc.connection).findByWalletId(12);

        assertEquals(1, transactions.size());
        Transaction transaction = transactions.get(0);
        assertEquals(91, transaction.getId());
        assertEquals(12, transaction.getWalletId());
        assertEquals(LocalDate.of(2026, 10, 1), transaction.getOperationDate());
        assertEquals(OperationType.CASH_IN, transaction.getOperationType());
        assertEquals(3.5, transaction.getQuantity());
        assertEquals(12, jdbc.parameters.get(1));
    }

    @Test
    void createsTransactionAndAssignsGeneratedId() {
        JdbcFixture jdbc = new JdbcFixture(List.of(), 31, false);
        Transaction transaction = new Transaction(5, LocalDate.of(2026, 10, 2), OperationType.CASH_OUT, 2.25);

        assertSame(transaction, new TransactionDAOImplMariaDB(jdbc.connection).create(transaction));

        assertEquals(31, transaction.getId());
        assertEquals(5, jdbc.parameters.get(1));
        assertEquals(Date.valueOf(LocalDate.of(2026, 10, 2)), jdbc.parameters.get(2));
        assertEquals("V", jdbc.parameters.get(3));
        assertEquals(2.25, jdbc.parameters.get(4));
        assertTrue(jdbc.sql.startsWith("INSERT INTO MOVIMENTACAO"));
    }

    @Test
    void findsOracleByDateAndReturnsNullWhenNotFound() {
        LocalDate date = LocalDate.of(2026, 10, 3);
        JdbcFixture jdbc = new JdbcFixture(List.of(Map.of(
                "Data", Date.valueOf(date), "Cotacao", 6.75)), null, false);

        Oracle oracle = new OracleMariaDBDAO(jdbc.connection).findByDate(date);

        assertEquals(date, oracle.getDate());
        assertEquals(6.75, oracle.getPrice());
        assertEquals(Date.valueOf(date), jdbc.parameters.get(1));

        JdbcFixture missing = new JdbcFixture(List.of(), null, false);
        assertNull(new OracleMariaDBDAO(missing.connection).findByDate(date));
    }

    @Test
    void returnsNullWhenOracleQueryFails() {
        JdbcFixture jdbc = new JdbcFixture(List.of(), null, true);

        assertNull(new OracleMariaDBDAO(jdbc.connection).findByDate(LocalDate.now()));
    }

    private static final class JdbcFixture {
        private final Map<Integer, Object> parameters = new HashMap<>();
        private String sql = "";
        private final Connection connection;

        JdbcFixture(List<Map<String, Object>> rows, Integer generatedId, boolean failOnPrepare) {
            ResultSet queryResult = resultSet(rows);
            List<Map<String, Object>> keys = generatedId == null
                    ? List.of()
                    : List.of(Map.of("1", generatedId));
            ResultSet generatedKeys = resultSet(keys);

            InvocationHandler statementHandler = (proxy, method, arguments) -> {
                String methodName = method.getName();
                if (methodName.startsWith("set") && arguments != null && arguments.length == 2) {
                    parameters.put((Integer) arguments[0], arguments[1]);
                    return null;
                }
                return switch (methodName) {
                    case "executeQuery" -> queryResult;
                    case "getGeneratedKeys" -> generatedKeys;
                    case "executeUpdate" -> 1;
                    default -> defaultValue(method.getReturnType());
                };
            };
            PreparedStatement statement = proxy(PreparedStatement.class, statementHandler);

            InvocationHandler connectionHandler = (proxy, method, arguments) -> {
                if (method.getName().equals("prepareStatement")) {
                    if (failOnPrepare) {
                        throw new SQLException("database unavailable");
                    }
                    sql = (String) arguments[0];
                    return statement;
                }
                return defaultValue(method.getReturnType());
            };
            connection = proxy(Connection.class, connectionHandler);
        }

        private static ResultSet resultSet(List<Map<String, Object>> rows) {
            int[] index = {-1};
            InvocationHandler handler = (proxy, method, arguments) -> switch (method.getName()) {
                case "next" -> ++index[0] < rows.size();
                case "getInt", "getDouble", "getString", "getDate" -> {
                    Object key = arguments[0];
                    Object value = rows.get(index[0]).get(String.valueOf(key));
                    yield value;
                }
                default -> defaultValue(method.getReturnType());
            };
            return proxy(ResultSet.class, handler);
        }

        @SuppressWarnings("unchecked")
        private static <T> T proxy(Class<T> type, InvocationHandler handler) {
            return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler);
        }

        private static Object defaultValue(Class<?> type) {
            if (!type.isPrimitive() || type == void.class) {
                return null;
            }
            if (type == boolean.class) {
                return false;
            }
            if (type == char.class) {
                return '\0';
            }
            if (type == byte.class) {
                return (byte) 0;
            }
            if (type == short.class) {
                return (short) 0;
            }
            if (type == int.class) {
                return 0;
            }
            if (type == long.class) {
                return 0L;
            }
            if (type == float.class) {
                return 0f;
            }
            return 0d;
        }
    }
}
