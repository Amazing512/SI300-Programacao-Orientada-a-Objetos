package org.unicamp.poo.dao;

import org.unicamp.poo.model.Wallet;

import java.util.List;

/**
 * Interface selada de Acesso a Dados (DAO) para operações de persistência e consulta de {@link Wallet}.
 * Define as operações do CRUD e consultas especializadas sobre carteiras de investimento.
 */
public sealed interface WalletDAO permits org.unicamp.poo.dao.impl.mariadb.WalletDAOImplMariaDB,
    org.unicamp.poo.dao.impl.memory.WalletMemoryDAO {

    /**
     * Persiste uma nova carteira no banco ou meio de armazenamento.
     *
     * @param wallet a carteira a ser criada
     * @return a carteira persistida com o ID gerado, ou {@code null} caso ocorra falha
     */
    Wallet create(Wallet wallet);

    /**
     * Localiza uma carteira através de seu identificador único.
     *
     * @param id o identificador da carteira desejada
     * @return a carteira encontrada ou {@code null} se não existir
     */
    Wallet findById(int id);

    /**
     * Retorna todas as carteiras cadastradas no repositório.
     *
     * @return uma lista com todas as carteiras existentes
     */
    List<Wallet> findAll();

    /**
     * Retorna todas as carteiras ordenadas pelo nome do titular em ordem alfabética ascendente.
     *
     * @return uma lista ordenada de carteiras pelo titular
     */
    List<Wallet> findAllOrderByHolder();

    /**
     * Atualiza as informações (titular e corretora) de uma carteira existente.
     *
     * @param wallet a carteira com os dados modificados
     */
    void update(Wallet wallet);

    /**
     * Remove a carteira associada ao identificador informado.
     *
     * @param id o identificador da carteira a ser excluída
     */
    void delete(int id);
}