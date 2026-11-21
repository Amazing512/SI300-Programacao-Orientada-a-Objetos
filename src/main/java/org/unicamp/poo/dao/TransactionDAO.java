package org.unicamp.poo.dao;

import org.unicamp.poo.model.Transaction;

import java.util.List;

/**
 * Interface selada de Acesso a Dados (DAO) para gerenciamento de persistência de {@link Transaction}.
 * Define as operações de gravação e consulta de movimentações financeiras de carteiras.
 */
public sealed interface TransactionDAO permits org.unicamp.poo.dao.impl.mariadb.TransactionDAOImplMariaDB,
    org.unicamp.poo.dao.impl.memory.TransactionMemoryDAO {

    /**
     * Persiste uma nova transação no mecanismo de armazenamento configurado.
     *
     * @param transaction a transação a ser persistida
     * @return a transação com seu identificador atribuído ou {@code null} se a gravação falhar
     */
    Transaction create(Transaction transaction);

    /**
     * Recupera todas as transações associadas a uma carteira informada.
     *
     * @param walletId o identificador da carteira
     * @return a lista de transações da carteira, podendo ser vazia caso nenhuma transação seja encontrada
     */
    List<Transaction> findByWalletId(int walletId);
}