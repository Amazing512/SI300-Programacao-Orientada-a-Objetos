package network;

import java.io.Serializable;
import java.util.List;

public final class NetworkResponse implements Serializable
   {
   private static final long serialVersionUID = 1L;

   public List<ExchangeRate> exchangeRates;
   public String             errorMessage;

   public NetworkResponse(List<ExchangeRate> exchangeRates, String errorMessage)
      {
      super();
      this.exchangeRates = exchangeRates;
      this.errorMessage  = errorMessage;
      }

   public final String getErrorMessage()
      {
      return errorMessage;
      }

   public final List<ExchangeRate> getExchangeRates()
      {
      return exchangeRates;
      }

   }
