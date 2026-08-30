package org.unicamp.poo.dao;

import org.unicamp.poo.model.Oracle;

import java.time.LocalDate;

public sealed interface OracleDAO permits org.unicamp.poo.controller.OracleController,
        org.unicamp.poo.dao.impl.mariadb.OracleMariaDBDAO,
        org.unicamp.poo.dao.impl.memory.OracleMemoryDAO {
    Oracle create(Oracle oracle);

    Oracle findByDate(LocalDate date);
}