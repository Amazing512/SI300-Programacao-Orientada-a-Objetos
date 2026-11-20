package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.controller.OracleController;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.oracle.OracleSimpleClient;

import network.ExchangeRate;

class OracleControllerTest {

    @Test
    void usesExistingQuoteAndCachesIt() {
        OracleController controller = new OracleController(new OracleSimpleClient());
        LocalDate today = LocalDate.now();
        Oracle existing = new Oracle(today, 6.5);
        controller.create(existing);

        Oracle first = controller.getOrGenerateDailyQuote();
        Oracle second = controller.getOrGenerateDailyQuote();

        assertSame(existing, first);
        assertSame(first, second);
        assertEquals(existing.getPrice(), first.getPrice());
    }

    @Test
    void generatesQuoteWhenDateDoesNotExist() {
        OracleController controller = new OracleController(new OracleSimpleClient());

        Oracle quote = controller.getOrGenerateDailyQuote();

        assertEquals(LocalDate.now(), quote.getDate());
        assertTrue(quote.getPrice() >= 1.0 && quote.getPrice() <= 10.0);
    }

    @Test
    void findsQuoteByDateAndReturnsNullWhenMissing() {
        OracleController controller = new OracleController(new OracleSimpleClient());
        LocalDate date = LocalDate.of(2026, 3, 15);
        Oracle expected = new Oracle(date, 4.0);
        controller.create(expected);

        assertSame(expected, controller.findByDate(date));
        assertNull(controller.findByDate(date.plusDays(1)));
    }

    @Test
    void loadsQuoteFromOracleSimpleClient() {
        LocalDate targetDate = LocalDate.of(2026, 5, 20);
        OracleSimpleClient mockClient = new OracleSimpleClient() {
            @Override
            public List<ExchangeRate> getValues(LocalDate since, LocalDate to) {
                return List.of(new ExchangeRate(targetDate, BigDecimal.valueOf(7.85)));
            }
        };

        OracleController controller = new OracleController(mockClient);
        Oracle loaded = controller.findByDate(targetDate);

        assertNotNull(loaded);
        assertEquals(targetDate, loaded.getDate());
        assertEquals(7.85, loaded.getPrice());

        // Segundo acesso deve vir do cache
        assertSame(loaded, controller.findByDate(targetDate));
    }
}
