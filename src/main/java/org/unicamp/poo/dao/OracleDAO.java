package org.unicamp.poo.dao;

import org.unicamp.poo.model.Oracle;

import java.util.Date;

public abstract interface OracleDAO {

    Oracle create(Oracle oracle);

    Oracle findByDate(Date date);
}