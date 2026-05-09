package org.unicamp.poo.model;

import java.io.Serializable;

public class Wallet implements Serializable {
    private final static long serialVersionUID = 1L;

    private Integer id;
    private String holder;
    private String broker;

    public Wallet(String holder, String broker) {
        super();
        this.id = null; // Recebido na persistência
        this.holder = holder;
        this.broker = broker;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHolder() {
        return this.holder;
    }

    public String getBroker() {
        return this.broker;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", holder='" + holder + '\'' +
                ", broker='" + broker + '\'' +
                '}';
    }
}
