package org.unicamp.poo.model;

import java.time.LocalDate;

public final class OracleFactory {

    private OracleFactory() {
        // Constructor privado
    }

    // Cria uma cotação do oráculo para a data especificada com o preço fornecido.
    public static Oracle create(LocalDate date, double price) {
        validate(date, price);
        return new Oracle(date, price);
    }

    // Cria uma cotação do oráculo para a data atual com o preço fornecido.
    public static Oracle createForToday(double price) {
        return create(LocalDate.now(), price);
    }

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
