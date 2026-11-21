package org.unicamp.poo.dao;

import java.sql.Connection;

/**
 * Interface selada que padroniza o ciclo de vida e a obtenção de conexões JDBC com o banco de dados.
 */
public sealed interface DatabaseConnection permits org.unicamp.poo.dao.impl.mariadb.MariaDBConnection {
    /**
     * Encerra a conexão ativa com o banco de dados, liberando os recursos alocados.
     */
    void closeConnection();

    /**
     * Obtém a conexão JDBC subjacente ativa.
     *
     * @return a instância de {@link Connection} ativa, ou {@code null} se a conexão não foi aberta ou falhou
     */
    Connection getConnection();

    /**
     * Estabelece e inicializa uma nova conexão com o banco de dados com base nas configurações configuradas.
     */
    void openConnection();
}
