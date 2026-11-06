package org.unicamp.poo.oracle;

/**
 * Falha ao consultar a API do oráculo (rede, HTTP, configuração ou formato da resposta).
 */
public class OracleApiException extends Exception {

    public OracleApiException(String message) {
        super(message);
    }

    public OracleApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
