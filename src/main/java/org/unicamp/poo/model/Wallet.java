package org.unicamp.poo.model;

import java.io.Serializable;

public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String holder;
    private String broker;

    public Wallet(int id, String holder, String broker) {
        this.id = id;
        this.holder = holder;
        this.broker = broker;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHolder() {
        return holder;
    }

    public String getBroker() {
        return broker;
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