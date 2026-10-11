package org.unicamp.poo.model;

import java.util.ResourceBundle;

public final class WalletFactory {

    private WalletFactory() {
        // Constructor privado
    }

    // Cria uma nova carteira com o titular e corretora especificados.
    public static Wallet create(String holder, String broker) {
        validate(holder, broker);
        return new Wallet(holder.trim(), broker.trim());
    }

    // Reconstitui uma carteira existente carregada do banco de dados.
    public static Wallet fromDatabase(int id, String holder, String broker) {
        validate(holder, broker);
        return new Wallet(id, holder.trim(), broker.trim());
    }

    // Cria uma nova carteira com dados atualizados, mantendo o mesmo ID.
    public static Wallet withUpdatedData(int id, String newHolder, String newBroker) {
        return fromDatabase(id, newHolder, newBroker);
    }

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
