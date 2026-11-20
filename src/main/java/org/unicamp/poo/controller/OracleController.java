package org.unicamp.poo.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.OraclePriceFactory;
import org.unicamp.poo.oracle.OracleSimpleClient;
import org.unicamp.poo.util.DailyCache;

import network.ExchangeRate;

/**
 * Fornece cotações da moeda virtual gerenciadas através do OracleSimpleClient.
 * Mantém cache em memória para cotação diária e cotações históricas a fim de reduzir chamadas de rede.
 */
public final class OracleController {
    private static final Logger log = LoggerFactory.getLogger(OracleController.class);

    private final OracleSimpleClient client;
    private final DailyCache<Oracle> todayCache;
    private final Map<LocalDate, Oracle> historicalCache;

    public OracleController() {
        this(new OracleSimpleClient());
    }

    public OracleController(OracleSimpleClient client) {
        super();
        this.client = client != null ? client : new OracleSimpleClient();
        this.todayCache = new DailyCache<>();
        this.historicalCache = new HashMap<>();
    }

    /**
     * Cotação de hoje (cache ou OracleSimpleClient).
     *
     * @return a cotação, ou cotação gerada se nenhuma fonte a tiver
     */
    public Oracle getOrGenerateDailyQuote() {
        final Oracle cached = todayCache.get();
        if (cached != null) {
            log.debug("Cotação diária recuperada do cache: R$ {}", cached.getPrice());
            return cached;
        }
        LocalDate today = LocalDate.now();
        final Oracle quote = loadQuote(today);
        if (quote != null) {
            log.info("Cotação diária localizada via OracleSimpleClient para a data {}: R$ {}", today, quote.getPrice());
            todayCache.put(quote);
            return quote;
        }

        // Se o oráculo não tiver a cotação de hoje, gera uma cotação fallback simulada
        double simulatedPrice = 1.0 + Math.random() * 9.0;
        Oracle fallbackQuote = OraclePriceFactory.create(today, simulatedPrice);
        todayCache.put(fallbackQuote);
        log.info("Cotação diária gerada para a data {}: R$ {}", today, fallbackQuote.getPrice());
        return fallbackQuote;
    }

    /**
     * Registra uma cotação no cache local (útil para testes e mock).
     */
    public Oracle create(Oracle oracle) {
        if (oracle == null) {
            return null;
        }
        log.info("Registrando cotação no cache do OracleController: Data={}, Preço={}", oracle.getDate(), oracle.getPrice());
        if (LocalDate.now().equals(oracle.getDate())) {
            todayCache.put(oracle);
        }
        historicalCache.put(oracle.getDate(), oracle);
        return oracle;
    }

    public Oracle findByDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        if (LocalDate.now().equals(date)) {
            return getOrGenerateDailyQuote();
        }
        final Oracle cached = historicalCache.get(date);
        if (cached != null) {
            log.debug("Cotação histórica para a data {} recuperada do cache: R$ {}",
                    date, cached.getPrice());
            return cached;
        }
        
        final Oracle quote = loadQuote(date);
        if (quote != null) {
            historicalCache.put(date, quote);
        }
        return quote;
    }

    /** Consulta a API remota via OracleSimpleClient. */
    private Oracle loadQuote(LocalDate date) {
        if (client == null) {
            return null;
        }
        try {
            final List<ExchangeRate> rates = client.getValues(date, date);
            if (rates != null && !rates.isEmpty()) {
                for (final ExchangeRate rate : rates) {
                    if (date.equals(rate.getDate()) && rate.getValue() != null) {
                        return OraclePriceFactory.create(rate.getDate(), rate.getValue().doubleValue());
                    }
                }
            }
            return null;
        } catch (Exception e) {
            log.warn("[Oráculo] Erro ao consultar cotação para {}: {}", date, e.getMessage());
            return null;
        }
    }
}
