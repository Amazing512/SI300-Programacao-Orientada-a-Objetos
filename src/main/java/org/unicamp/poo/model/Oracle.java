    package org.unicamp.poo.model;

import java.time.LocalDate;

    public class Oracle {
        private final LocalDate date;
        private final double price;

        public Oracle(LocalDate date, double price) {
            this.date = date;
            this.price = price;
        }

        public LocalDate getDate() {
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
