module org.unicamp.poo {
    requires java.sql;
    requires org.mariadb.jdbc;

    exports org.unicamp.poo.controller;
    exports org.unicamp.poo.dao;
    exports org.unicamp.poo.dao.impl.mariadb;
    exports org.unicamp.poo.dao.impl.memory;
    exports org.unicamp.poo.main;
    exports org.unicamp.poo.model;
    exports org.unicamp.poo.model.enums;
    exports org.unicamp.poo.util;
    exports org.unicamp.poo.view;
}