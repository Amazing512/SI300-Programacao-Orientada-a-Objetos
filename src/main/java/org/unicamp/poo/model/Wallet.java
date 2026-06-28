package org.unicamp.poo.model;

public class Wallet {
    private Integer id;
    private final String holder;
    private final String broker;

    public Wallet(Integer id, String holder, String broker) {
        this.id = id;
        this.holder = holder;
        this.broker = broker;
    }

    public Wallet(String holder, String broker) {
        this.id = null;
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