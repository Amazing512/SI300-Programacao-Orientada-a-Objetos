package org.unicamp.poo.model;

/**
 * Representa uma carteira de investimentos (Wallet) no sistema.
 */
public class Wallet {
    private Integer id;
    private final String holder;
    private final String broker;

    /**
     * Constrói uma instância de carteira com id, titular e corretora definidos.
     *
     * @param id     o id único da carteira
     * @param holder o nome do titular responsável pela carteira
     * @param broker a corretora associada à carteira
     */
    public Wallet(Integer id, String holder, String broker) {
        this.id = id;
        this.holder = holder;
        this.broker = broker;
    }

    /**
     * Constrói uma nova carteira com titular e corretora, sem id atribuído.
     *
     * @param holder o nome do titular responsável pela carteira
     * @param broker a corretora associada à carteira
     */
    public Wallet(String holder, String broker) {
        this.id = null;
        this.holder = holder;
        this.broker = broker;
    }

    /**
     * Obtém o id único da carteira.
     *
     * @return o id único ou null se ainda não persistido
     */
    public Integer getId() {
        return id;
    }

    /**
     * Define o id único da carteira.
     *
     * @param id o id gerado pelo mecanismo de persistência
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtém o nome do titular da carteira.
     *
     * @return o nome do titular
     */
    public String getHolder() {
        return holder;
    }

    /**
     * Obtém o nome da corretora associada à carteira.
     *
     * @return o nome da corretora
     */
    public String getBroker() {
        return broker;
    }

    /**
     * Retorna a representação em formato textual dos dados da carteira.
     *
     * @return representação em {@link String} contendo id, titular e corretora
     */
    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", holder='" + holder + '\'' +
                ", broker='" + broker + '\'' +
                '}';
    }
}