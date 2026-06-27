package org.unicamp.poo.controller;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/* Internal controller responsible for providing the daily virtual-coin quote (Oracle).
   This controller has no menu and is never accessed directly by the user: it is only
   consulted internally by other controllers (e.g. TransactionController) right before
   a buy or sell operation is confirmed. */

public class OracleController {

    // Simulated price range used when generating a quote automatically (in R$)
    private static final double MIN_SIMULATED_PRICE = 1.0;
    private static final double MAX_SIMULATED_PRICE = 10.0;

    private final OracleDAO model;
    private final Random random;

    // Constructor using Dependency Injection to initialize the controller components.
    public OracleController(OracleDAO model) {
        super();
        this.model = model;
        this.random = new Random();
    }

    // Removes the time portion of a Date so quotes are always keyed by calendar day.
    private Date normalizeToDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // Generates a simulated quote value within the configured price range.
    private double generateSimulatedPrice() {
        return MIN_SIMULATED_PRICE + (random.nextDouble() * (MAX_SIMULATED_PRICE - MIN_SIMULATED_PRICE));
    }

    /* Returns today's quote, generating and persisting a simulated one automatically
       the first time it is requested if none exists yet for the current day.
       Returns null only if persistence itself fails, which callers should treat as
       "quote unavailable" and block the operation in progress. */
    public Oracle getOrGenerateDailyQuote() {
        Date today = normalizeToDay(new Date());

        Oracle existingQuote = model.findByDate(today);
        if (existingQuote != null) {
            return existingQuote;
        }

        Oracle simulatedQuote = new Oracle(today, generateSimulatedPrice());
        return model.create(simulatedQuote);
    }
}
