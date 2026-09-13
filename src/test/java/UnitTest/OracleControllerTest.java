package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.controller.OracleController;
import org.unicamp.poo.dao.impl.memory.OracleMemoryDAO;
import org.unicamp.poo.model.Oracle;

class OracleControllerTest {

    @Test
    void usesExistingQuoteAndCachesIt() {
        OracleMemoryDAO dao = new OracleMemoryDAO();
        LocalDate today = LocalDate.now();
        Oracle existing = dao.findByDate(today);
        if (existing == null) {
            existing = dao.create(new Oracle(today, 6.5));
        }
        OracleController controller = new OracleController(dao);

        Oracle first = controller.getOrGenerateDailyQuote();
        Oracle second = controller.getOrGenerateDailyQuote();

        assertSame(existing, first);
        assertSame(first, second);
        assertEquals(existing.getPrice(), first.getPrice());
    }

    @Test
    void generatesQuoteWhenDateDoesNotExist() {
        OracleController controller = new OracleController(new OracleMemoryDAO());

        Oracle quote = controller.getOrGenerateDailyQuote();

        assertEquals(LocalDate.now(), quote.getDate());
        assertTrue(quote.getPrice() >= 1.0 && quote.getPrice() <= 10.0);
    }

    @Test
    void findsQuoteByDateAndReturnsNullWhenMissing() {
        OracleMemoryDAO dao = new OracleMemoryDAO();
        LocalDate date = LocalDate.of(2026, 3, 15);
        Oracle expected = new Oracle(date, 4.0);
        dao.create(expected);
        OracleController controller = new OracleController(dao);

        assertSame(expected, controller.findByDate(date));
        assertEquals(null, controller.findByDate(date.plusDays(1)));
    }
}