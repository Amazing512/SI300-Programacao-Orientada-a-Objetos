package IntegrationTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.unicamp.poo.oracle.OracleSimpleClient;

import network.ExchangeRate;

@EnabledIfSystemProperty(named = "oracle.integration", matches = "true")
class OracleApiIntegrationTest {

    @Test
    void retrievesValidRatesFromOracleApi() throws Exception {
        OracleSimpleClient client = new OracleSimpleClient();
        LocalDate to = LocalDate.now();
        LocalDate since = to.minusDays(30);

        List<ExchangeRate> rates = client.getValues(since, to);

        assertNotNull(rates, "API must return a rate list");
        assertFalse(rates.isEmpty(), "API must return at least one rate in the last 30 days");
        for (ExchangeRate rate : rates) {
            assertNotNull(rate, "API must not return null rates");
            assertNotNull(rate.getDate(), "Each rate must include a date");
            assertNotNull(rate.getValue(), "Each rate must include a value");
            assertTrue(!rate.getDate().isBefore(since) && !rate.getDate().isAfter(to),
                    "Each rate date must be within the requested range");
            assertTrue(rate.getValue().signum() > 0, "Each rate value must be positive");
        }
    }
}
