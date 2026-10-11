package org.unicamp.poo.model;

import java.time.LocalDate;
import java.util.ResourceBundle;
import org.unicamp.poo.model.enums.OperationType;

public final class TransactionFactory {

    private TransactionFactory() {
       // Constructor privado
    }

    // Cria uma transação de compra (CASH_IN).
    public static Transaction createBuy(int walletId, LocalDate date, double quantity) {
        return create(walletId, date, OperationType.CASH_IN, quantity);
    }

    // Cria uma transação de venda (CASH_OUT).
    public static Transaction createSell(int walletId, LocalDate date, double quantity) {
        return create(walletId, date, OperationType.CASH_OUT, quantity);
    }

    // Cria uma transação genérica com os detalhes especificados.
    public static Transaction create(int walletId, LocalDate date, OperationType type, double quantity) {
        validate(quantity, date, type);
        return new Transaction(walletId, date, type, quantity);
    }

    // Reconstitui uma transação existente carregada do banco de dados.
    public static Transaction fromDatabase(int id, int walletId, LocalDate date, OperationType type, double quantity) {
        validate(quantity, date, type);
        Transaction transaction = new Transaction(walletId, date, type, quantity);
        transaction.setId(id);
        return transaction;
    }

    private static void validate(double quantity, LocalDate date, OperationType type) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(getMessage("validation.transaction.quantity.positive"));
        }
        if (date == null) {
            throw new IllegalArgumentException(getMessage("validation.transaction.date.required"));
        }
        if (type == null) {
            throw new IllegalArgumentException(getMessage("validation.transaction.type.required"));
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
