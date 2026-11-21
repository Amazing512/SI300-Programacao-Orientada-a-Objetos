package org.unicamp.poo.model;

import java.time.LocalDate;

/**
 * Representa uma cotação.
 */
public class Oracle {
    private final LocalDate date;
    private final double price;

    /**
     * Constrói uma nova instância de cotação.
     *
     * @param date  a data da cotação
     * @param price o valor monetário da moeda na data informada
     */
    public Oracle(LocalDate date, double price) {
        this.date = date;
        this.price = price;
    }

    /**
     * Obtém a data da cotação.
     *
     * @return a data correspondente
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Obtém o preço da moeda na data da cotação.
     *
     * @return o preço da cotação
     */
    public double getPrice() {
        return price;
    }

    /**
     * Retorna a representação em texto da cotação.
     *
     * @return representação em {@link String} com a data e o preço
     */
    @Override
    public String toString() {
        return "Oracle{" +
                "date=" + date +
                ", price=" + price +
                '}';
    }
}
