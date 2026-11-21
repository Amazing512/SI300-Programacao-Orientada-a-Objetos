package org.unicamp.poo.model;

import java.time.LocalDate;
import java.util.ResourceBundle;
import org.unicamp.poo.model.enums.OperationType;

/**
 * Fábrica responsável pela instanciação e validação de objetos {@link Transaction}.
 */
public final class TransactionFactory {

    private TransactionFactory() {
    }

    /**
     * Cria uma transação do tipo compra.
     *
     * @param walletId o id da carteira de destino
     * @param date     a data em que a compra ocorreu
     * @param quantity a quantidade de moedas compradas
     * @return uma nova instância de {@link Transaction}
     * @throws IllegalArgumentException se a quantidade for menor ou igual a zero, ou se a data for nula
     */
    public static Transaction createBuy(int walletId, LocalDate date, double quantity) {
        return create(walletId, date, OperationType.CASH_IN, quantity);
    }

    /**
     * Cria uma transação do tipo venda.
     *
     * @param walletId o id da carteira de origem
     * @param date     a data em que a venda ocorreu
     * @param quantity a quantidade de moedas vendidas
     * @return uma nova instância de {@link Transaction}
     * @throws IllegalArgumentException se a quantidade for menor ou igual a zero, ou se a data for nula
     */
    public static Transaction createSell(int walletId, LocalDate date, double quantity) {
        return create(walletId, date, OperationType.CASH_OUT, quantity);
    }

    /**
     * Cria e valida uma transação para a carteira informada.
     *
     * @param walletId o id da carteira associada
     * @param date     a data da transação
     * @param type     o tipo da operação (compra ou venda)
     * @param quantity a quantidade de moedas negociadas
     * @return a nova transação instanciada
     * @throws IllegalArgumentException se a quantidade for menor ou igual a zero, ou se data ou tipo forem nulos
     */
    public static Transaction create(int walletId, LocalDate date, OperationType type, double quantity) {
        validate(quantity, date, type);
        return new Transaction(walletId, date, type, quantity);
    }

    /**
     * Reconstrói uma transação persistida a partir dos dados do banco de dados.
     *
     * @param id       o id persistido da transação
     * @param walletId o id da carteira associada
     * @param date     a data da transação
     * @param type     o tipo da operação
     * @param quantity a quantidade negociada
     * @return a transação reconstituída com o respectivo ID
     * @throws IllegalArgumentException se qualquer parâmetro for inválido
     */
    public static Transaction fromDatabase(int id, int walletId, LocalDate date, OperationType type, double quantity) {
        validate(quantity, date, type);
        Transaction transaction = new Transaction(walletId, date, type, quantity);
        transaction.setId(id);
        return transaction;
    }

    /**
     * Valida os campos de quantidade, data e tipo de uma transação.
     *
     * @param quantity a quantidade negociada
     * @param date     a data da operação
     * @param type     o tipo da operação
     * @throws IllegalArgumentException caso alguma regra de negócio seja violada
     */
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
