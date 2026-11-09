package org.unicamp.poo.oracle;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuração do servidor do oráculo. Endereço e porta vêm de
 * {@code OracleServer.properties} (classpath); usuário e senha do grupo vêm das variáveis de ambiente
 * ORACLE_GROUP_NAME e ORACLE_GROUP_PASSWORD ou do arquivo local {@code oracle.env}
 * (ignorado pelo Git), nunca do código-fonte.
 *
 * @param host          endereço do servidor
 * @param port          porta TCP do servidor do oráculo (não é a porta do MariaDB)
 * @param groupName     nome do grupo, usado na autenticação
 * @param groupPassword senha do grupo
 * @param timeoutMillis tempo máximo de conexão e de espera pela resposta
 */
public record OracleApiConfig(String host, int port, String groupName, String groupPassword, int timeoutMillis) {

    public static final String DEFAULT_RESOURCE = "OracleServer";
    public static final String GROUP_NAME_ENV = "ORACLE_GROUP_NAME";
    public static final String GROUP_PASSWORD_ENV = "ORACLE_GROUP_PASSWORD";
    private static final String CREDENTIALS_FILE = "oracle.env";
    private static final int DEFAULT_TIMEOUT_MILLIS = 5000;

    public static OracleApiConfig load(String resourceName) throws OracleApiException {
        final Properties props = new Properties();
        try (InputStream input = OracleApiConfig.class.getClassLoader()
                .getResourceAsStream(resourceName + ".properties")) {
            if (input == null) {
                throw new OracleApiException("Arquivo de configuração não encontrado: " + resourceName + ".properties");
            }
            props.load(input);
        } catch (IOException e) {
            throw new OracleApiException("Erro ao ler a configuração do oráculo", e);
        }

        final Properties credentials = new Properties();
        try (InputStream input = new FileInputStream(CREDENTIALS_FILE)) {
            credentials.load(input);
        } catch (IOException e) {
            // arquivo opcional: as variáveis de ambiente podem bastar
        }

        final String host = props.getProperty("serverIP", "").trim();
        final String group = firstNonBlank(System.getenv(GROUP_NAME_ENV), credentials.getProperty(GROUP_NAME_ENV));
        final String password = firstNonBlank(System.getenv(GROUP_PASSWORD_ENV), credentials.getProperty(GROUP_PASSWORD_ENV));
        if (host.isEmpty()) {
            throw new OracleApiException("'serverIP' do oráculo não configurado");
        }
        if (group.isEmpty() || password.isEmpty()) {
            throw new OracleApiException("Credenciais do oráculo ausentes (" + GROUP_NAME_ENV + " / " + GROUP_PASSWORD_ENV + ")");
        }
        return new OracleApiConfig(host, parseInt(props.getProperty("serverPort"), -1, "serverPort"), group, password,
                parseInt(props.getProperty("timeoutMillis"), DEFAULT_TIMEOUT_MILLIS, "timeoutMillis"));
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a.trim();
        }
        return b == null ? "" : b.trim();
    }

    private static int parseInt(String value, int fallback, String name) throws OracleApiException {
        if (value == null || value.isBlank()) {
            if (fallback < 0) {
                throw new OracleApiException("'" + name + "' do oráculo não configurado");
            }
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new OracleApiException("Valor inválido para '" + name + "': " + value, e);
        }
    }
}
