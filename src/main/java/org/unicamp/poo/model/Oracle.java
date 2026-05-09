package org.unicamp.poo.model;

import java.io.Serializable;
import java.util.Date;

public class Oracle implements Serializable {
    private final static long serialVersionUID = 1L;

    private Date date;
    private double price;

    public Oracle(Date date, double price) {
        this.date = date;
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Oracle{" +
                "date=" + date +
                ", price=" + price +
                '}';
    }
}
