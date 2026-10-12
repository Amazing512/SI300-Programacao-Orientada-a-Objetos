package org.unicamp.poo.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.OracleFactory;
import org.unicamp.poo.util.DailyCache;

/* 
 * Controller responsável por lidar com operações relacionadas ao Oráculo, que fornece a cotação diária da moeda virtual.
 *
 * Estratégia de cache em duas camadas:
 *   1. todayCache (DailyCache)    — guarda a cotação de HOJE. Expira automaticamente
 *                                   ao virar o dia (compara LocalDate.now() com a data
 *                                   em que o valor foi salvo).
 *   2. historicalCache (HashMap)  — guarda cotações de dias PASSADOS consultados durante
 *                                   a execução. Nunca expira, pois cotações passadas são
 *                                   imutáveis — não faz sentido buscá-las novamente no banco.
 */
public final class OracleController implements OracleDAO {

    // Range de preços simulado para a cotação diária da moeda virtual
    private static final double MIN_SIMULATED_PRICE = 1.0;
    private static final double MAX_SIMULATED_PRICE = 10.0;

    private final OracleDAO model;
    private final Random random;

    // Cache da cotação de HOJE: expira automaticamente ao virar o dia
    private final DailyCache<Oracle> todayCache;

    // Cache de cotações de DIAS PASSADOS: persiste durante toda a execução
    private final Map<LocalDate, Oracle> historicalCache;

    public OracleController(OracleDAO model) {
        super();
        this.model = model;
        this.random = new Random();
        this.todayCache = new DailyCache<>();
        this.historicalCache = new HashMap<>();
    }

    // Gera uma cotação aleatória dentro do intervalo definido para simulação.
    private double generateSimulatedPrice() {
        return MIN_SIMULATED_PRICE + (random.nextDouble() * (MAX_SIMULATED_PRICE - MIN_SIMULATED_PRICE));
    }

    /**
     * Retorna a cotação do dia atual usando o DailyCache.
     * Se o cache de hoje estiver vazio ou expirado, busca no banco.
     * Se não existir no banco, gera uma cotação simulada via OracleFactory e a persiste.
     */
    public Oracle getOrGenerateDailyQuote() {
        // 1. Verifica o cache do dia — retorna imediatamente se válido
        Oracle cached = todayCache.get();
        if (cached != null) {
            return cached;
        }

        // 2. Cache expirado ou vazio: busca no banco de dados
        LocalDate today = LocalDate.now();
        Oracle existing = model.findByDate(today);
        if (existing != null) {
            todayCache.put(existing); // Guarda no cache do dia
            return existing;
        }

        // 3. Não existe no banco: usa OracleFactory para criar (com validação), persiste e guarda no cache
        Oracle simulatedQuote = OracleFactory.create(today, generateSimulatedPrice());
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

    /**
     * Busca a cotação de uma data específica.
     * - Se for hoje: delega ao getOrGenerateDailyQuote() (usa DailyCache).
     * - Se for dia passado: usa o historicalCache. Se não estiver em cache,
     *   busca no banco e guarda para o resto da sessão.
     */
    @Override
    public Oracle findByDate(LocalDate date) {
        // Cotação de hoje: usa a lógica dedicada com DailyCache
        if (LocalDate.now().equals(date)) {
            return getOrGenerateDailyQuote();
        }

        // Cotação de dia passado: verifica o cache histórico primeiro
        if (historicalCache.containsKey(date)) {
            return historicalCache.get(date);
        }

        // Não está em cache: busca no banco e guarda para o resto da sessão
        Oracle quote = model.findByDate(date);
        if (quote != null) {
            historicalCache.put(date, quote); // Persiste até o programa fechar
        }
        return quote;
    }
}
