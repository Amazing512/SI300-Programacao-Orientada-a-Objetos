package org.unicamp.poo.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.oracle.OracleApiClient;
import org.unicamp.poo.oracle.OracleApiException;
import org.unicamp.poo.oracle.SimulatedOracleApiClient;
import org.unicamp.poo.util.DailyCache;

/**
 * Fornece cotações da moeda virtual. Ordem de consulta: cache, banco (DAO) e,
 * por último, a API do oráculo; o que vem da API é gravado no banco e no cache
 * para reduzir chamadas de rede.
 */
public final class OracleController implements OracleDAO {

    private final OracleDAO model;
    private final OracleApiClient apiClient;
    private final DailyCache<Oracle> todayCache;
    private final Map<LocalDate, Oracle> historicalCache;

    /** Usa o oráculo simulado (somente hoje). Mantido para testes e uso offline. */
    public OracleController(OracleDAO model) {
        this(model, new SimulatedOracleApiClient());
    }

    public OracleController(OracleDAO model, OracleApiClient apiClient) {
        super();
        this.model = model;
        this.apiClient = apiClient;
        this.todayCache = new DailyCache<>();
        this.historicalCache = new HashMap<>();
    }

    /**
     * Cotação de hoje (cache, banco ou API).
     *
     * @return a cotação, ou {@code null} se nenhuma fonte a tiver (erro vai para System.err)
     */
    public Oracle getOrGenerateDailyQuote() {
        final Oracle cached = todayCache.get();
        if (cached != null) {
            return cached;
        }
        final Oracle quote = loadQuote(LocalDate.now());
        if (quote != null) {
            todayCache.put(quote);
        }
        return quote;
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
        final Oracle cached = historicalCache.get(date);
        if (cached != null) {
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
        try {
            final Oracle fetched = apiClient.fetchQuote(date);
            final Oracle saved = model.create(fetched);
            // Se não conseguiu gravar, ainda assim usa a cotação obtida da API.
            return saved != null ? saved : fetched;
        } catch (OracleApiException e) {
            System.err.println("[Oráculo] " + date + ": " + e.getMessage());
            return null;
        }
    }
}
