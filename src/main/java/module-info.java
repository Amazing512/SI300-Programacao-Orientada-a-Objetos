module org.unicamp.poo {
    requires transitive java.desktop;
    requires transitive java.sql;
    requires org.mariadb.jdbc;
    requires org.slf4j;

    exports org.unicamp.poo.controller;
    exports org.unicamp.poo.dao;
    exports org.unicamp.poo.dao.impl.mariadb;
    exports org.unicamp.poo.dao.impl.memory;
    exports org.unicamp.poo.main;
    exports org.unicamp.poo.model;
    exports org.unicamp.poo.model.enums;
    exports org.unicamp.poo.oracle;
    exports network;
    exports org.unicamp.poo.util;
    exports org.unicamp.poo.view;
}
