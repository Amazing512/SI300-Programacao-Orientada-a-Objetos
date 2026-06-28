    package org.unicamp.poo.model;

    import java.util.Date;

    public class Oracle {
        private final Date date;
        private final double price;

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
