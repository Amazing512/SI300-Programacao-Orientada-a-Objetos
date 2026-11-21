package org.unicamp.poo.util;

import java.time.LocalDate;

/**
 * Estrutura genérica de cache com expiração diária.
 * Armazena um valor associado à data atual do sistema, considerando-o válido apenas enquanto
 * o dia corrente for idêntico à data em que o valor foi armazenado.
 *
 * @param <V> o tipo do objeto armazenado no cache
 */
public final class DailyCache<V> {

    private V cachedValue;
    private LocalDate cacheDate;

    /**
     * Inicializa uma nova instância de cache diário vazio.
     */
    public DailyCache() {
        this.cachedValue = null;
        this.cacheDate = null;
    }

    /**
     * Verifica se o cache possui um elemento e se ele foi gravado no dia de hoje.
     *
     * @return {@code true} se o dado for válido para o dia atual; {@code false} se inexistente ou expirado
     */
    public boolean isValid() {
        return cachedValue != null && LocalDate.now().equals(cacheDate);
    }

    /**
     * Recupera o valor armazenado se este ainda for válido para o dia de hoje.
     *
     * @return o elemento em cache, ou null se o cache estiver inválido ou expirado
     */
    public V get() {
        return isValid() ? cachedValue : null;
    }

    /**
     * Armazena um valor no cache associando-o à data de hoje ({@link LocalDate#now()}).
     *
     * @param value o elemento a ser armazenado
     */
    public void put(V value) {
        this.cachedValue = value;
        this.cacheDate = LocalDate.now();
    }

    /**
     * Limpa o valor em cache, forçando sua expiração imediata.
     */
    public void invalidate() {
        this.cachedValue = null;
        this.cacheDate = null;
    }
}
