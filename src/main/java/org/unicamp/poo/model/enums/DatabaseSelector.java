package org.unicamp.poo.model.enums;

/**
 * Define o tipo de mecanismo de persistência de dados utilizado pelo sistema.
 */
public enum DatabaseSelector {
    /**
     * Persistência volátil em memória principal, ideal para testes ou execuções sem servidor de banco.
     */
    MEMORY,

    /**
     * Persistência em banco de dados relacional MariaDB via conexão JDBC.
     */
    MARIADB
}
