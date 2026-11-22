package org.unicamp.poo.dao.impl.memory;

import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.model.Wallet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementação em memória de {@link WalletDAO}.
 * Mantém a coleção de carteiras em memória primária para uso quando a persistência relacional não está ativa.
 */
public final class WalletMemoryDAO implements WalletDAO {

    private static final List<Wallet> wallets = new ArrayList<>();
    private static int nextId = 1;

    /**
     * Construtor padrão para o DAO de carteiras em memória.
     */
    public WalletMemoryDAO() {
    }

    /**
     * Adiciona uma nova carteira na lista em memória e atribui um ID sequencial.
     *
     * @param wallet a carteira a ser salva
     * @return a carteira com seu ID gerado
     */
    @Override
    public Wallet create(Wallet wallet) {
        wallet.setId(nextId++);
        wallets.add(wallet);
        return wallet;
    }

    /**
     * Localiza uma carteira na memória pelo seu ID.
     *
     * @param id o identificador da carteira
     * @return a carteira correspondente ou null se não for encontrada
     */
    @Override
    public Wallet findById(int id) {
        for (Wallet wallet : wallets) {
            if (wallet.getId() == id) {
                return wallet;
            }
        }
        return null;
    }

    /**
     * Retorna uma cópia contendo todas as carteiras armazenadas em memória.
     *
     * @return lista com todas as carteiras
     */
    @Override
    public List<Wallet> findAll() {
        return new ArrayList<>(wallets);
    }

    /**
     * Retorna a lista de carteiras ordenadas alfabeticamente pelo titular.
     *
     * @return lista ordenada de carteiras
     */
    @Override
    public List<Wallet> findAllOrderByHolder() {
        List<Wallet> orderedList = new ArrayList<>(wallets);

        orderedList.sort(Comparator.comparing(Wallet::getHolder));

        return orderedList;
    }

    /**
     * Substitui os dados da carteira em memória com o mesmo ID da carteira fornecida.
     *
     * @param updatedWallet a carteira contendo as informações atualizadas
     */
    @Override
    public boolean update(Wallet updatedWallet) {
        for (int walletIndex = 0; walletIndex < wallets.size(); walletIndex++) {
            Wallet currentWallet = wallets.get(walletIndex);

            if (currentWallet.getId().equals(updatedWallet.getId())) {
                wallets.set(walletIndex, updatedWallet);
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a carteira associada ao ID informado da memória.
     *
     * @param id o identificador da carteira a ser removida
     */
    @Override
    public boolean delete(int id) {
        return wallets.removeIf(wallet -> wallet.getId() == id);
    }
}