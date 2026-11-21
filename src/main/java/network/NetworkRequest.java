package network;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Representa uma requisição de rede enviada ao servidor do Oráculo para consulta
 * de cotações da FTCoin em um determinado intervalo de datas.
 */
public final class NetworkRequest implements Serializable
   {
   private static final long serialVersionUID = 1L;

   /**
    * Nome de identificação do grupo requisitante para autenticação no Oráculo.
    */
   private final String      groupName;

   /**
    * Senha do grupo requisitante para autenticação no Oráculo.
    */
   private final String      groupPassword;

   /**
    * Data inicial do intervalo de consulta.
    */
   private final LocalDate   firstDate;

   /**
    * Data final do intervalo de consulta.
    */
   private final LocalDate   lastDate;

   /**
    * Constrói uma nova requisição com as credenciais de autenticação e o período de busca.
    *
    * @param groupName     o nome de identificação do grupo requisitante
    * @param groupPassword a senha de autenticação do grupo
    * @param firstDate     a data inicial do período de consulta
    * @param lastDate      a data final do período de consulta
    */
   public NetworkRequest(String groupName, String groupPassword, LocalDate firstDate, LocalDate lastDate)
      {
      super();
      this.groupName     = groupName;
      this.groupPassword = groupPassword;
      this.firstDate     = firstDate;
      this.lastDate      = lastDate;
      }

   /**
    * Obtém a data inicial do período consultado.
    *
    * @return a data inicial da consulta
    */
   public final LocalDate getFirstDate()
      {
      return firstDate;
      }

   /**
    * Obtém o nome do grupo requisitante.
    *
    * @return o nome do grupo
    */
   public final String getGroupName()
      {
      return groupName;
      }

   /**
    * Obtém a senha do grupo.
    *
    * @return a senha do grupo
    */
   public final String getGroupPassword()
      {
      return groupPassword;
      }

   /**
    * Obtém a data final do período consultado.
    *
    * @return a data final da consulta
    */
   public final LocalDate getLastDate()
      {
      return lastDate;
      }

   }
