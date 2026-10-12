package org.unicamp.poo.util;

import java.time.LocalDate;

/**
 * Cache de valor único com validade de 1 dia.
 *
 * O valor armazenado é considerado válido apenas enquanto {@code LocalDate.now()}
 * for igual à data em que ele foi guardado. Ao virar a meia-noite e a data
 * mudar, {@code get()} passa a retornar {@code null} automaticamente —
 * sem necessidade de timer ou thread externa.
 *
 * @param <V> Tipo do valor armazenado no cache.
 */
public final class DailyCache<V> {

    // O valor guardado e o dia em que ele foi armazenado
    private V cachedValue;
    private LocalDate cacheDate;

    public DailyCache() {
        this.cachedValue = null;
        this.cacheDate = null;
    }

    /**
     * Retorna {@code true} se o cache contém um valor válido para o dia de hoje.
     * A comparação é feita com {@code LocalDate.now()}, então a expiração é
     * automática ao virar o dia.
     */
    public boolean isValid() {
        return cachedValue != null && LocalDate.now().equals(cacheDate);
    }

    /**
     * Retorna o valor em cache se ainda for válido para hoje,
     * ou {@code null} caso o cache esteja vazio ou tenha expirado.
     */
    public V get() {
        return isValid() ? cachedValue : null;
    }

    /**
     * Armazena um valor no cache, marcado com a data de hoje.
     * Na próxima virada de dia, este valor será automaticamente ignorado.
     */
    public void put(V value) {
        this.cachedValue = value;
        this.cacheDate = LocalDate.now();
    }

    /**
     * Invalida o cache manualmente, descartando o valor armazenado.
     * Útil principalmente em testes unitários.
     */
    public void invalidate() {
        this.cachedValue = null;
        this.cacheDate = null;
    }
}
