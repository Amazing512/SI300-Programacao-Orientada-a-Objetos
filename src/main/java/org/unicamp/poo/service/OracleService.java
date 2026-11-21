package org.unicamp.poo.service;

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
 * Serviço de domínio responsável pelo gerenciamento de cotações da moeda virtual através do OracleSimpleClient.
 */
public class OracleService {
    private static final Logger log = LoggerFactory.getLogger(OracleService.class);

    private final OracleSimpleClient client;
    private final DailyCache<Oracle> todayCache;
    private final Map<LocalDate, Oracle> historicalCache;

    /**
     * Construtor padrão que inicializa o serviço com um novo {@link OracleSimpleClient}.
     */
    public OracleService() {
        this(new OracleSimpleClient());
    }

    /**
     * Construtor parametrizado que permite injetar uma instância customizada de {@link OracleSimpleClient}.
     *
     * @param client o cliente do Oráculo a ser utilizado para as requisições de rede
     */
    public OracleService(OracleSimpleClient client) {
        super();
        this.client = client != null ? client : new OracleSimpleClient();
        this.todayCache = new DailyCache<>();
        this.historicalCache = new HashMap<>();
    }

    /**
     * Obtém a cotação de hoje (via cache ou OracleSimpleClient).
     *
     * @return a cotação diária
     * @throws IllegalStateException se a cotação não for encontrada
     */
    public Oracle getDailyQuote() {
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

        log.error("Cotação não encontrada para a data de hoje: {}", today);
        throw new IllegalStateException("Cotação não encontrada para a data de hoje: " + today);
    }

    /**
     * Registra uma cotação no cache local.
     *
     * @param oracle a cotação a ser registrada no cache
     * @return a cotação registrada ou null se o objeto for nulo
     */
    public Oracle create(Oracle oracle) {
        if (oracle == null) {
            return null;
        }
        log.info("Registrando cotação no cache do OracleService: Data={}, Preço={}", oracle.getDate(), oracle.getPrice());
        if (LocalDate.now().equals(oracle.getDate())) {
            todayCache.put(oracle);
        }
        historicalCache.put(oracle.getDate(), oracle);
        return oracle;
    }

    /**
     * Consulta cotação para uma data específica.
     *
     * @param date a data da cotação a ser consultada
     * @return a cotação correspondente ou null caso não seja encontrada
     */
    public Oracle findByDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        if (LocalDate.now().equals(date)) {
            try {
                return getDailyQuote();
            } catch (IllegalStateException e) {
                return null;
            }
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
