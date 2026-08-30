package org.unicamp.poo.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;

/* 
 * Controller responsável por lidar com operações relacionadas ao Oráculo, que fornece a cotação diária da moeda virtual.
*/

public class OracleController implements OracleDAO {

    // Range de preços simulado para a cotação diária da moeda virtual
    private static final double MIN_SIMULATED_PRICE = 1.0;
    private static final double MAX_SIMULATED_PRICE = 10.0;

    private final OracleDAO model;
    private final Random random;

    private final Map<LocalDate, Oracle> cache;

    public OracleController(OracleDAO model) {
        super();
        this.model = model;
        this.random = new Random();
        this.cache = new HashMap<>();
    }

    // LocalDate já não contém horas, minutos ou segundos, então não precisa normalizar
    private LocalDate normalizeToDay(LocalDate date) {
        return date;
    }

    // Gera uma cotação aleatória dentro do intervalo definido para simulação.
    private double generateSimulatedPrice() {
        return MIN_SIMULATED_PRICE + (random.nextDouble() * (MAX_SIMULATED_PRICE - MIN_SIMULATED_PRICE));
    }

    /* Pega a cotação diária ou gera uma aleatória se ainda não existir no banco */
    public Oracle getOrGenerateDailyQuote() {
        LocalDate today = normalizeToDay(LocalDate.now());

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

    public Oracle findByDate(LocalDate date) {
        LocalDate normalizedDate = normalizeToDay(date);
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
