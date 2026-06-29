package org.unicamp.poo.dao;

import org.unicamp.poo.model.Oracle;

import java.util.Date;

public abstract class OracleDAO {

    public abstract Oracle create(Oracle oracle);

    public abstract Oracle findByDate(Date date);
}