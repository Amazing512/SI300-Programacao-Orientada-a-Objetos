package org.unicamp.poo.oracle;

import java.time.LocalDate;
import java.util.List;

import org.unicamp.poo.model.Oracle;

/**
 * Contrato de acesso à fonte externa de cotações (a API do professor).
 * Permite trocar a implementação de rede por uma simulada nos testes.
 */
public interface OracleApiClient {

    /**
     * Consulta as cotações de um intervalo de datas (inclusive).
     *
     * @return lista possivelmente vazia, nunca nula
     * @throws OracleApiException se o servidor estiver inacessível ou responder com erro
     */
    List<Oracle> fetchQuotes(LocalDate firstDate, LocalDate lastDate) throws OracleApiException;

    /**
     * Consulta a cotação de um único dia.
     *
     * @throws OracleApiException se houver falha de comunicação ou o servidor não tiver cotação do dia
     */
    default Oracle fetchQuote(LocalDate date) throws OracleApiException {
        for (final Oracle quote : fetchQuotes(date, date)) {
            if (date.equals(quote.getDate())) {
                return quote;
            }
        }
        throw new OracleApiException("O servidor não possui cotação para " + date);
    }
}
