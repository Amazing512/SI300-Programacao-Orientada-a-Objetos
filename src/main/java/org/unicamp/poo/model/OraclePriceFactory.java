package org.unicamp.poo.model;

import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Fábrica responsável pela instanciação e validação de objetos de {@link Oracle}.
 */
public final class OraclePriceFactory {

    private OraclePriceFactory() {
    }

    /**
     * Cria e valida uma cotação para a data e preço especificados.
     *
     * @param date  a data de referência da cotação
     * @param price o preço da moeda na data
     * @return a nova cotação {@link Oracle} instanciada
     * @throws IllegalArgumentException se a data for nula ou o preço for menor ou igual a zero
     */
    public static Oracle create(LocalDate date, double price) {
        validate(date, price);
        return new Oracle(date, price);
    }

    /**
     * Cria e valida uma cotação utilizando a data de hoje.
     *
     * @param price o preço da moeda no dia de hoje
     * @return a nova cotação {@link Oracle} instanciada para o dia atual
     * @throws IllegalArgumentException se o preço for menor ou igual a zero
     */
    public static Oracle createForToday(double price) {
        return create(LocalDate.now(), price);
    }

    /**
     * Valida os parâmetros de data e preço da cotação.
     *
     * @param date  a data da cotação
     * @param price o preço informado
     * @throws IllegalArgumentException se a data for nula ou o preço for menor ou igual a zero
     */
    private static void validate(LocalDate date, double price) {
        if (date == null) {
            throw new IllegalArgumentException(getMessage("validation.oracle.date.required"));
        }
        if (price <= 0) {
            throw new IllegalArgumentException(getMessage("validation.oracle.price.positive"));
        }
    }

    private static String getMessage(String key) {
        try {
            return ResourceBundle.getBundle("messages").getString(key);
        } catch (Exception exception) {
            return key;
        }
    }
}
