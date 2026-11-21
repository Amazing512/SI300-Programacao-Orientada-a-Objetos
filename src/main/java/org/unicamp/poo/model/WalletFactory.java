package org.unicamp.poo.model;

import java.util.ResourceBundle;

/**
 * Fábrica responsável pela criação e validação de instâncias de {@link Wallet}.
 */
public final class WalletFactory {

    private WalletFactory() {
    }

    /**
     * Cria uma nova carteira a partir do nome do titular e da corretora informados.
     *
     * @param holder o nome do titular da carteira
     * @param broker o nome da corretora associada
     * @return uma nova instância de {@link Wallet} sem id
     * @throws IllegalArgumentException se o titular ou a corretora forem nulos ou vazios
     */
    public static Wallet create(String holder, String broker) {
        validate(holder, broker);
        return new Wallet(holder.trim(), broker.trim());
    }

    /**
     * Reconstrói uma carteira existente a partir dos dados recuperados do banco de dados.
     *
     * @param id     o id da carteira no banco de dados
     * @param holder o nome do titular da carteira
     * @param broker o nome da corretora associada
     * @return a instância de {@link Wallet} reconstituída com seu respectivo id
     * @throws IllegalArgumentException se o titular ou a corretora forem nulos ou vazios
     */
    public static Wallet fromDatabase(int id, String holder, String broker) {
        validate(holder, broker);
        return new Wallet(id, holder.trim(), broker.trim());
    }

    /**
     * Cria uma instância de carteira refletindo dados atualizados de titular e corretora para o id especificado.
     *
     * @param id        o id da carteira a ser atualizada
     * @param newHolder o novo nome do titular
     * @param newBroker a nova corretora associada
     * @return a carteira com os dados atualizados
     * @throws IllegalArgumentException se os novos dados forem inválidos
     */
    public static Wallet withUpdatedData(int id, String newHolder, String newBroker) {
        return fromDatabase(id, newHolder, newBroker);
    }

    /**
     * Valida os campos obrigatórios de uma carteira.
     *
     * @param holder o nome do titular
     * @param broker o nome da corretora
     * @throws IllegalArgumentException se algum dos campos for nulo ou vazio
     */
    private static void validate(String holder, String broker) {
        if (holder == null || holder.isBlank()) {
            throw new IllegalArgumentException(getMessage("validation.wallet.holder.required"));
        }
        if (broker == null || broker.isBlank()) {
            throw new IllegalArgumentException(getMessage("validation.wallet.broker.required"));
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
