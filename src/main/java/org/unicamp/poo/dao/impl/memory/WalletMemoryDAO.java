package org.unicamp.poo.dao.impl.memory;

import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.model.Wallet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WalletMemoryDAO extends WalletDAO {

    private static final List<Wallet> wallets = new ArrayList<>();
    private static int nextId = 1;

    @Override
    public Wallet create(Wallet wallet) {
        wallet.setId(nextId++);
        wallets.add(wallet);
        return wallet;
    }

    @Override
    public Wallet findById(int id) {
        for (Wallet wallet : wallets) {
            if (wallet.getId() == id) {
                return wallet;
            }
        }
        return null;
    }

    @Override
    public List<Wallet> findAll() {
        return new ArrayList<>(wallets);
    }

    @Override
    public List<Wallet> findAllOrderByHolder() {
        List<Wallet> orderedList = new ArrayList<>(wallets);

        orderedList.sort(Comparator.comparing(Wallet::getHolder));

        return orderedList;
    }

    @Override
    public void update(Wallet updatedWallet) {
        for (int i = 0; i < wallets.size(); i++) {
            Wallet currentWallet = wallets.get(i);

            if (currentWallet.getId().equals(updatedWallet.getId())) {
                wallets.set(i, updatedWallet);
                return;
            }
        }
    }

    @Override
    public void delete(int id) {
        wallets.removeIf(wallet -> wallet.getId() == id);
    }
}