package org.unicamp.poo.dao;

import org.unicamp.poo.model.Wallet;

import java.util.List;

public interface WalletDAO {

    Wallet create(Wallet wallet);

    Wallet findById(int id);

    List<Wallet> findAll();

    List<Wallet> findAllOrderByHolder();

    void update(Wallet wallet);

    void delete(int id);
}