/*
 * ************************************************************************************************************************
 * Prof. Dr. André F. de Angelis
 * Unicamp - Universidade Estadual de Campinas
 * FT - Faculdade de Tecnologia
 * ************************************************************************************************************************
 * Escrito em Java 20, usando IDE Eclipse 2023-06, em ambiente Windows 11
 *
 * Versão 1.0
 * 07.Outubro.2026
 *
 * ************************************************************************************************************************
 *
 * Este classe é o DTO de um sistema cliente/servidor (cotação da FTCoin entre servidor e cliente). Projeto FTCoin, ver. 2026.
 *
 * ************************************************************************************************************************
 */

package network;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Objeto de Transferência de Dados (DTO) que representa a cotação da moeda FTCoin
 * em uma determinada data.
 */
public final class ExchangeRate implements Serializable
   {
   private static final long serialVersionUID = 1L;

   /**
    * Data de referência da cotação da moeda.
    */
   LocalDate                 date;

   /**
    * Valor monetário da cotação na data especificada.
    */
   BigDecimal                value;

   /**
    * Constrói uma nova cotação com data e valor.
    *
    * @param date  a data da cotação
    * @param value o valor monetário da cotação
    */
   public ExchangeRate(LocalDate date, BigDecimal value)
      {
      super();
      this.date  = date;
      this.value = value;
      }

   /**
    * Obtém a data da cotação.
    *
    * @return a data da cotação
    */
   public final LocalDate getDate()
      {
      return date;
      }

   /**
    * Obtém o valor da cotação da moeda.
    *
    * @return o valor da cotação
    */
   public final BigDecimal getValue()
      {
      return value;
      }

   }
