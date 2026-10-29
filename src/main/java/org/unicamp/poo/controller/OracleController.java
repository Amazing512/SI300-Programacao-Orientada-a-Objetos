package org.unicamp.poo.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.OraclePriceFactory;
import org.unicamp.poo.util.DailyCache;

public final class OracleController implements OracleDAO {

    private static final double MIN_SIMULATED_PRICE = 1.0;
    private static final double MAX_SIMULATED_PRICE = 10.0;

    private final OracleDAO model;
    private final Random random;

    private final DailyCache<Oracle> todayCache;

    private final Map<LocalDate, Oracle> historicalCache;

    public OracleController(OracleDAO model) {
        super();
        this.model = model;
        this.random = new Random();
        this.todayCache = new DailyCache<>();
        this.historicalCache = new HashMap<>();
    }

    private double generateSimulatedPrice() {
        return MIN_SIMULATED_PRICE + (random.nextDouble() * (MAX_SIMULATED_PRICE - MIN_SIMULATED_PRICE));
    }

    public Oracle getOrGenerateDailyQuote() {
        Oracle cached = todayCache.get();
        if (cached != null) {
            return cached;
        }

        LocalDate today = LocalDate.now();
        Oracle existing = model.findByDate(today);
        if (existing != null) {
            todayCache.put(existing);
            return existing;
        }

        Oracle simulatedQuote = OraclePriceFactory.create(today, generateSimulatedPrice());
        Oracle createdQuote = model.create(simulatedQuote);
        if (createdQuote != null) {
            todayCache.put(createdQuote);
        }
        return createdQuote;
    }

    @Override
    public Oracle create(Oracle oracle) {
        return model.create(oracle);
    }

    @Override
    public Oracle findByDate(LocalDate date) {
        if (LocalDate.now().equals(date)) {
            return getOrGenerateDailyQuote();
        }

        if (historicalCache.containsKey(date)) {
            return historicalCache.get(date);
        }

        Oracle quote = model.findByDate(date);
        if (quote != null) {
            historicalCache.put(date, quote);
        }
        return quote;
    }
}
