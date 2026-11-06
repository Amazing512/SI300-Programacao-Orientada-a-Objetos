package org.unicamp.poo.oracle;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.OraclePriceFactory;

/**
 * Oráculo simulado para testes e uso offline: só conhece a data de hoje, com preço aleatório.
 */
public final class SimulatedOracleApiClient implements OracleApiClient {

    private static final double MIN_PRICE = 1.0;
    private static final double MAX_PRICE = 10.0;

    private final Random random = new Random();

    @Override
    public List<Oracle> fetchQuotes(LocalDate firstDate, LocalDate lastDate) {
        final LocalDate today = LocalDate.now();
        if (today.isBefore(firstDate) || today.isAfter(lastDate)) {
            return List.of();
        }
        return List.of(OraclePriceFactory.create(today, MIN_PRICE + random.nextDouble() * (MAX_PRICE - MIN_PRICE)));
    }
}
