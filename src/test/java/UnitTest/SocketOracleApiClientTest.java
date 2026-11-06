package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.controller.OracleController;
import org.unicamp.poo.dao.impl.memory.OracleMemoryDAO;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.oracle.OracleApiConfig;
import org.unicamp.poo.oracle.OracleApiException;
import org.unicamp.poo.oracle.SocketOracleApiClient;

import network.ExchangeRate;
import network.NetworkRequest;
import network.NetworkResponse;

/** Testa o cliente contra um servidor falso local que fala o mesmo protocolo do professor. */
class SocketOracleApiClientTest {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 7);

    /** Servidor de uma resposta por conexão. O handler devolve o objeto a enviar (ou null para não responder). */
    private static final class FakeServer implements AutoCloseable {
        final ServerSocket socket;
        final AtomicInteger connections = new AtomicInteger();
        final AtomicReference<NetworkRequest> lastRequest = new AtomicReference<>();

        FakeServer(Function<NetworkRequest, Object> handler) throws Exception {
            socket = new ServerSocket(0, 50, java.net.InetAddress.getLoopbackAddress());
            Thread thread = new Thread(() -> {
                while (!socket.isClosed()) {
                    try (Socket client = socket.accept();
                         ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                         ObjectInputStream in = new ObjectInputStream(client.getInputStream())) {
                        connections.incrementAndGet();
                        NetworkRequest request = (NetworkRequest) in.readObject();
                        lastRequest.set(request);
                        Object reply = handler.apply(request);
                        if (reply == null) {
                            Thread.sleep(2000);
                        } else {
                            out.writeObject(reply);
                            out.flush();
                        }
                    } catch (Exception ignored) {
                        // cliente desconectou
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }

        SocketOracleApiClient client() {
            return new SocketOracleApiClient(new OracleApiConfig(
                    "127.0.0.1", socket.getLocalPort(), "G1", "pw", 500));
        }

        @Override
        public void close() throws Exception {
            socket.close();
        }
    }

    private static NetworkResponse ok(ExchangeRate... rates) {
        return new NetworkResponse(List.of(rates), null);
    }

    @Test
    void sendsCredentialsAndDatesAndConvertsRates() throws Exception {
        try (FakeServer server = new FakeServer(r -> ok(new ExchangeRate(DAY, new BigDecimal("5.43"))))) {
            List<Oracle> quotes = server.client().fetchQuotes(DAY.minusDays(7), DAY);

            assertEquals(1, quotes.size());
            assertEquals(DAY, quotes.get(0).getDate());
            assertEquals(5.43, quotes.get(0).getPrice());
            assertEquals("G1", server.lastRequest.get().getGroupName());
            assertEquals("pw", server.lastRequest.get().getGroupPassword());
            assertEquals(DAY.minusDays(7), server.lastRequest.get().getFirstDate());
            assertEquals(DAY, server.lastRequest.get().getLastDate());
        }
    }

    @Test
    void emptyResponseMeansNoQuoteForTheDay() throws Exception {
        try (FakeServer server = new FakeServer(r -> ok())) {
            assertTrue(server.client().fetchQuotes(DAY, DAY).isEmpty());
            assertThrows(OracleApiException.class, () -> server.client().fetchQuote(DAY));
        }
    }

    @Test
    void serverErrorMessageBecomesException() throws Exception {
        try (FakeServer server = new FakeServer(r -> new NetworkResponse(null, "senha inválida"))) {
            OracleApiException e = assertThrows(OracleApiException.class, () -> server.client().fetchQuote(DAY));
            assertTrue(e.getMessage().contains("senha inválida"));
        }
    }

    @Test
    void invalidPriceFromServerIsRejected() throws Exception {
        try (FakeServer server = new FakeServer(r -> ok(new ExchangeRate(DAY, BigDecimal.ZERO)))) {
            assertThrows(OracleApiException.class, () -> server.client().fetchQuote(DAY));
        }
    }

    @Test
    void unreachableServerAndSilentServerFailCleanly() throws Exception {
        SocketOracleApiClient closed;
        try (FakeServer server = new FakeServer(r -> ok())) {
            closed = server.client();
        }
        assertThrows(OracleApiException.class, () -> closed.fetchQuote(DAY));

        try (FakeServer silent = new FakeServer(r -> null)) {
            assertThrows(OracleApiException.class, () -> silent.client().fetchQuote(DAY)); // timeout de leitura
        }
    }

    @Test
    void unexpectedClassInResponseIsRefused() throws Exception {
        try (FakeServer server = new FakeServer(r -> new AtomicInteger(1))) {
            assertThrows(OracleApiException.class, () -> server.client().fetchQuote(DAY));
        }
    }

    @Test
    void controllerAsksServerOnceThenUsesCacheAndDatabase() throws Exception {
        try (FakeServer server = new FakeServer(r -> ok(new ExchangeRate(DAY, new BigDecimal("4.20"))))) {
            OracleMemoryDAO dao = new OracleMemoryDAO();
            OracleController controller = new OracleController(dao, server.client());

            Oracle first = controller.findByDate(DAY);
            assertSame(first, controller.findByDate(DAY));

            assertEquals(1, server.connections.get());
            assertEquals(4.2, dao.findByDate(DAY).getPrice());
        }
    }

    @Test
    void controllerReturnsNullWhenServerIsDown() throws Exception {
        SocketOracleApiClient down;
        try (FakeServer server = new FakeServer(r -> ok())) {
            down = server.client();
        }
        assertNull(new OracleController(new OracleMemoryDAO(), down).findByDate(DAY.minusYears(5)));
    }
}
