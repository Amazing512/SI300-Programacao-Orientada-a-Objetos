package org.unicamp.poo.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;

/* 
 * Controller responsável por lidar com operações relacionadas ao Oráculo, que fornece a cotação diária da moeda virtual.
*/

public class OracleController {

    // Range de preços simulado para a cotação diária da moeda virtual
    private static final double MIN_SIMULATED_PRICE = 1.0;
    private static final double MAX_SIMULATED_PRICE = 10.0;

    private final OracleDAO model;
    private final Random random;

    private final Map<Date, Oracle> cache;

    public OracleController(OracleDAO model) {
        super();
        this.model = model;
        this.random = new Random();
        this.cache = new HashMap<>();
    }

    // Remove a hora da data
    private Date normalizeToDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // Gera uma cotação aleatória dentro do intervalo definido para simulação.
    private double generateSimulatedPrice() {
        return MIN_SIMULATED_PRICE + (random.nextDouble() * (MAX_SIMULATED_PRICE - MIN_SIMULATED_PRICE));
    }

    /* Pega a cotação diária ou gera uma aleatória se ainda não existir no banco */
    public Oracle getOrGenerateDailyQuote() {
        Date today = normalizeToDay(new Date());

        if (cache.containsKey(today)) {
            return cache.get(today);
        }

        Oracle existingQuote = model.findByDate(today);
        if (existingQuote != null) {
            cache.put(today, existingQuote);
            return existingQuote;
        }

        Oracle simulatedQuote = new Oracle(today, generateSimulatedPrice());
        Oracle createdQuote = model.create(simulatedQuote);
        if (createdQuote != null) {
            cache.put(today, createdQuote);
        }
        return createdQuote;
    }

    public Oracle findByDate(Date date) {
        Date normalizedDate = normalizeToDay(date);
        if (cache.containsKey(normalizedDate)) {
            return cache.get(normalizedDate);
        }

        Oracle quote = model.findByDate(normalizedDate);
        if (quote != null) {
            cache.put(normalizedDate, quote);
        }
        return quote;
    }
}
