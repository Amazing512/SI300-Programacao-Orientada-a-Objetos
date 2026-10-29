package org.unicamp.poo.util;

import java.time.LocalDate;

public final class DailyCache<V> {

    private V cachedValue;
    private LocalDate cacheDate;

    public DailyCache() {
        this.cachedValue = null;
        this.cacheDate = null;
    }

    public boolean isValid() {
        return cachedValue != null && LocalDate.now().equals(cacheDate);
    }

    public V get() {
        return isValid() ? cachedValue : null;
    }

    public void put(V value) {
        this.cachedValue = value;
        this.cacheDate = LocalDate.now();
    }

    public void invalidate() {
        this.cachedValue = null;
        this.cacheDate = null;
    }
}
