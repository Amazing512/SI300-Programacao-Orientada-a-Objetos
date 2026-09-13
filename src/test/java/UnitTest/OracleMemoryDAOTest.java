package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.dao.impl.memory.OracleMemoryDAO;
import org.unicamp.poo.model.Oracle;

class OracleMemoryDAOTest {

    @Test
    void createsAndFindsQuoteByLocalDate() {
        OracleMemoryDAO dao = new OracleMemoryDAO();
        LocalDate date = LocalDate.of(2026, 2, 20);
        Oracle quote = new Oracle(date, 7.25);

        dao.create(quote);

        assertEquals(quote, dao.findByDate(date));
        assertNull(dao.findByDate(date.plusDays(1)));
    }
}