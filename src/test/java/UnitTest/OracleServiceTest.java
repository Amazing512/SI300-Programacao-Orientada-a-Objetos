package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.oracle.OracleSimpleClient;
import org.unicamp.poo.service.OracleService;

import network.ExchangeRate;

class OracleServiceTest {

    @Test
    void usesExistingQuoteAndCachesIt() {
        OracleService service = new OracleService(new OracleSimpleClient());
        LocalDate today = LocalDate.now();
        Oracle existing = new Oracle(today, 6.5);
        service.create(existing);

        Oracle first = service.getDailyQuote();
        Oracle second = service.getDailyQuote();

        assertSame(existing, first);
        assertSame(first, second);
        assertEquals(existing.getPrice(), first.getPrice());
    }

    @Test
    void throwsExceptionWhenDailyQuoteNotFound() {
        OracleService service = new OracleService(emptyClient());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                service::getDailyQuote
        );
        assertTrue(exception.getMessage().contains("Cotação não encontrada"));
    }

    @Test
    void findsQuoteByDateAndReturnsNullWhenMissing() {
        OracleService service = new OracleService(emptyClient());
        LocalDate date = LocalDate.of(2026, 3, 15);
        Oracle expected = new Oracle(date, 4.0);
        service.create(expected);

        assertSame(expected, service.findByDate(date));
        assertNull(service.findByDate(date.plusDays(1)));
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

        OracleService service = new OracleService(mockClient);
        Oracle loaded = service.findByDate(targetDate);

        assertNotNull(loaded);
        assertEquals(targetDate, loaded.getDate());
        assertEquals(7.85, loaded.getPrice());

        // Segundo acesso deve vir do cache
        assertSame(loaded, service.findByDate(targetDate));
    }

    private static OracleSimpleClient emptyClient() {
        return new OracleSimpleClient() {
            @Override
            public List<ExchangeRate> getValues(LocalDate since, LocalDate to) {
                return List.of();
            }
        };
    }

}
