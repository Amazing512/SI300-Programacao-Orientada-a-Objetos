package org.unicamp.poo.oracle;

import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import network.ExchangeRate;
import network.NetworkRequest;
import network.NetworkResponse;

import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.OraclePriceFactory;

/**
 * Cliente da API proprietária do oráculo: envia um {@link NetworkRequest} por socket TCP
 * e recebe um {@link NetworkResponse} com objetos {@link ExchangeRate}.
 * Uma conexão curta é aberta por consulta, como no cliente de exemplo do professor.
 */
public final class SocketOracleApiClient implements OracleApiClient {

    /** Só estas classes podem ser desserializadas da rede. */
    private static final ObjectInputFilter RESPONSE_FILTER = ObjectInputFilter.Config.createFilter(
            "network.*;java.util.*;java.time.*;java.math.BigDecimal;java.math.BigInteger;java.lang.*;!*");

    private final OracleApiConfig config;

    public SocketOracleApiClient(OracleApiConfig config) {
        this.config = config;
    }

    @Override
    public List<Oracle> fetchQuotes(LocalDate firstDate, LocalDate lastDate) throws OracleApiException {
        final NetworkRequest request = new NetworkRequest(
                config.groupName(), config.groupPassword(), firstDate, lastDate);
        final NetworkResponse response = send(request);

        if (response.getErrorMessage() != null) {
            throw new OracleApiException("Servidor do oráculo: " + response.getErrorMessage());
        }
        return toOracles(response.getExchangeRates());
    }

    private NetworkResponse send(NetworkRequest request) throws OracleApiException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(config.host(), config.port()), config.timeoutMillis());
            socket.setSoTimeout(config.timeoutMillis());
            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                in.setObjectInputFilter(RESPONSE_FILTER);
                out.writeObject(request);
                out.flush();
                final Object received = in.readObject();
                if (received instanceof NetworkResponse response) {
                    return response;
                }
                throw new OracleApiException("Resposta inesperada do servidor do oráculo");
            }
        } catch (IOException e) {
            throw new OracleApiException("Servidor do oráculo indisponível em " + config.host() + ":" + config.port()
                    + " (" + e.getClass().getSimpleName() + (e.getMessage() != null ? ": " + e.getMessage() : "") + ")", e);
        } catch (ClassNotFoundException e) {
            throw new OracleApiException("Resposta do oráculo em formato desconhecido", e);
        }
    }

    private List<Oracle> toOracles(List<ExchangeRate> rates) throws OracleApiException {
        final List<Oracle> quotes = new ArrayList<>();
        if (rates == null) {
            return quotes;
        }
        for (final ExchangeRate rate : rates) {
            final BigDecimal value = rate.getValue();
            if (rate.getDate() == null || value == null) {
                throw new OracleApiException("Cotação incompleta recebida do servidor");
            }
            try {
                quotes.add(OraclePriceFactory.create(rate.getDate(), value.doubleValue()));
            } catch (IllegalArgumentException e) {
                throw new OracleApiException("Cotação inválida recebida do servidor: " + e.getMessage(), e);
            }
        }
        return quotes;
    }
}
