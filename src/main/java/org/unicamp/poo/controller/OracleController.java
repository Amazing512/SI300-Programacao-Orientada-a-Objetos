package org.unicamp.poo.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.OraclePriceFactory;
import org.unicamp.poo.oracle.OracleSimpleClient;
import org.unicamp.poo.util.DailyCache;

import network.ExchangeRate;

/**
 * Fornece cotações da moeda virtual. Ordem de consulta: cache, banco (DAO) e,
 * por último, a API do oráculo; o que vem da API é gravado no banco e no cache
 * para reduzir chamadas de rede.
 */
public final class OracleController implements OracleDAO {
    private static final Logger log = LoggerFactory.getLogger(OracleController.class);

    private final OracleDAO model;
    private final OracleSimpleClient client;
    private final DailyCache<Oracle> todayCache;
    private final Map<LocalDate, Oracle> historicalCache;

    public OracleController(OracleDAO model) {
        this(model, new OracleSimpleClient());
    }

    public OracleController(OracleDAO model, OracleSimpleClient client) {
        super();
        this.model = model;
        this.client = client;
        this.todayCache = new DailyCache<>();
        this.historicalCache = new HashMap<>();
    }

    /**
     * Cotação de hoje (cache, banco ou API).
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
            log.info("Cotação diária localizada para a data {}: R$ {}", today, quote.getPrice());
            todayCache.put(quote);
            return quote;
        }

        // Se nem o banco nem o oráculo tiverem a cotação de hoje, gera uma cotação fallback
        double simulatedPrice = 1.0 + Math.random() * 9.0;
        Oracle fallbackQuote = OraclePriceFactory.create(today, simulatedPrice);
        Oracle created = model.create(fallbackQuote);
        Oracle toCache = created != null ? created : fallbackQuote;
        todayCache.put(toCache);
        log.info("Cotação diária gerada para a data {}: R$ {}", today, toCache.getPrice());
        return toCache;
    }

    @Override
    public Oracle create(Oracle oracle) {
        log.info("Criando cotação via OracleController: Data={}, Preço={}", oracle.getDate(), oracle.getPrice());
        return model.create(oracle);
    }

    @Override
    public Oracle findByDate(LocalDate date) {
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

    /** Banco primeiro; se não houver, consulta a API e persiste o resultado. */
    private Oracle loadQuote(LocalDate date) {
        final Oracle stored = model.findByDate(date);
        if (stored != null) {
            return stored;
        }
        if (client == null) {
            return null;
        }
        try {
            final List<ExchangeRate> rates = client.getValues(date, date);
            if (rates != null && !rates.isEmpty()) {
                for (final ExchangeRate rate : rates) {
                    if (date.equals(rate.getDate()) && rate.getValue() != null) {
                        final Oracle oracle = OraclePriceFactory.create(rate.getDate(), rate.getValue().doubleValue());
                        final Oracle saved = model.create(oracle);
                        return saved != null ? saved : oracle;
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
