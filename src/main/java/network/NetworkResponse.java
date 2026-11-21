package network;

import java.io.Serializable;
import java.util.List;

/**
 * Representa a resposta retornada pelo servidor do Oráculo com a lista
 * de cotações solicitadas ou uma mensagem de erro.
 */
public final class NetworkResponse implements Serializable
   {
   private static final long serialVersionUID = 1L;

   /**
    * Lista de cotações retornadas pelo servidor.
    */
   public List<ExchangeRate> exchangeRates;

   /**
    * Mensagem de erro retornada pelo servidor em caso de falha.
    */
   public String             errorMessage;

   /**
    * Constrói uma nova resposta de rede com as cotações e eventual mensagem de erro.
    *
    * @param exchangeRates a lista de cotações retornadas
    * @param errorMessage  a mensagem de erro, ou {@code null} caso a requisição tenha obtido sucesso
    */
   public NetworkResponse(List<ExchangeRate> exchangeRates, String errorMessage)
      {
      super();
      this.exchangeRates = exchangeRates;
      this.errorMessage  = errorMessage;
      }

   /**
    * Obtém a mensagem de erro da resposta.
    *
    * @return a mensagem de erro ou {@code null} se não houve erro
    */
   public final String getErrorMessage()
      {
      return errorMessage;
      }

   /**
    * Obtém a lista de taxas de cotação retornadas.
    *
    * @return a lista de {@link ExchangeRate}
    */
   public final List<ExchangeRate> getExchangeRates()
      {
      return exchangeRates;
      }

   }
