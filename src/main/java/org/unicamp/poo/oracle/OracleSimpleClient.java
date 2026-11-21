package org.unicamp.poo.oracle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import network.ExchangeRate;
import network.NetworkRequest;
import network.NetworkResponse;

/**
 * Cliente de comunicação via sockets com o servidor do Oráculo de cotações da FTCoin.
 */
public class OracleSimpleClient
   {
   private static final Logger log = LoggerFactory.getLogger(OracleSimpleClient.class);

   private String host;
   private int    port;
   private int    timeoutMillis;
   private String groupName;
   private String groupPassword;

   /**
    * Construtor padrão que inicializa o cliente carregando as configurações do arquivo
    * {@code OracleServer.properties} e credenciais de variáveis de ambiente ou {@code oracle.env}.
    */
   public OracleSimpleClient()
      {
      this(null, 0);
      }

   /**
    * Constrói o cliente especificando o endereço de host e a porta de conexão do servidor.
    *
    * @param host o endereço de IP ou hostname do servidor Oráculo
    * @param port a porta de escuta do serviço remoto
    */
   public OracleSimpleClient(String host, int port)
      {
      final Properties props = new Properties();
      final Properties credentials = new Properties();

      try (InputStream input = getClass().getClassLoader().getResourceAsStream("OracleServer.properties"))
         {
         if (input != null)
            {
            props.load(input);
            }
         else
            {
            log.error("Arquivo OracleServer.properties não encontrado.");
            }

         this.host = host == null || host.isBlank() ? props.getProperty("serverIP") : host;
         this.port = host == null ? parseIntegerProperty(props, "serverPort") : port;
         timeoutMillis = parseIntegerProperty(props, "timeoutMillis");

         groupName = System.getenv("ORACLE_GROUP_NAME");
         groupPassword = System.getenv("ORACLE_GROUP_PASSWORD");

         if ((isBlank(groupName) || isBlank(groupPassword)) && loadCredentialsFromFile(credentials))
            {
            if (isBlank(groupName))
               {
               groupName = credentials.getProperty("ORACLE_GROUP_NAME");
               }
            if (isBlank(groupPassword))
               {
               groupPassword = credentials.getProperty("ORACLE_GROUP_PASSWORD");
               }
            }

         if ((groupName == null || groupName.isBlank()) || (groupPassword == null || groupPassword.isBlank()))
            {
            log.warn("Credenciais do Oráculo não encontradas em variáveis de ambiente nem em oracle.env.");
            }
         }
      catch (final Exception exceptionValue)
         {
         log.error("Erro ao carregar configurações do Oráculo: {}", exceptionValue.getMessage(), exceptionValue);
         }
      }

   private int parseIntegerProperty(Properties props, String propertyName)
      {
      try
         {
         return Integer.parseInt(props.getProperty(propertyName, "0").trim());
         }
      catch (final NumberFormatException exceptionValue)
         {
         log.error("Valor inválido para {} nas configurações do Oráculo.", propertyName, exceptionValue);
         return 0;
         }
      }

   private boolean isBlank(String value)
      {
      return value == null || value.isBlank();
      }

   /**
    * Executa uma consulta de teste diretamente via linha de comando.
    *
    * @param args argumentos de execução, podendo ser vazios ou conter [host porta]
    */
   public static void main(String[] args)
      {
      try
         {
         final OracleSimpleClient client;
         if (args.length == 2)
            {
            client = new OracleSimpleClient(args[0], Integer.parseInt(args[1]));
            }
         else if (args.length == 0)
            {
            client = new OracleSimpleClient();
            }
         else
            {
            System.err.println("Uso: OracleSimpleClient [host porta]");
            System.exit(2);
            return;
            }

         final LocalDate lastDate = LocalDate.now();
         final LocalDate firstDate = lastDate.minusDays(7);
         final List<ExchangeRate> records = client.getValues(firstDate, lastDate);
         if (records.isEmpty())
            {
            System.out.println("No records found");
            }
         else
            {
            for (final ExchangeRate record : records)
               {
               System.out.printf("%s, %.2f%n", record.getDate(), record.getValue());
               }
            }
         }
      catch (final Exception exceptionValue)
         {
         System.err.println("Client encountered error: " + exceptionValue.getMessage());
         System.exit(1);
         }
      }

   /**
    * Consulta o servidor remoto do Oráculo para obter a lista de cotações em um período.
    *
    * @param since a data inicial da consulta
    * @param to    a data final da consulta
    * @return a lista de {@link ExchangeRate} retornadas pelo Oráculo
    * @throws Exception se ocorrer erro de validação de parâmetros, comunicação via socket ou erro retornado pelo servidor
    */
   public List<ExchangeRate> getValues(LocalDate since, LocalDate to) throws Exception
      {
      if (isBlank(host) || port <= 0 || port > 65535)
         {
         throw new IllegalStateException("Servidor do Oráculo não configurado (host ou porta inválidos).");
         }
      if (timeoutMillis <= 0)
         {
         throw new IllegalStateException("Timeout do Oráculo não configurado ou inválido.");
         }
      if (isBlank(groupName) || isBlank(groupPassword))
         {
         throw new IllegalStateException("Credenciais do Oráculo não configuradas.");
         }

      final NetworkRequest  request  = new NetworkRequest(groupName, groupPassword, since, to);
      final NetworkResponse response = sendRequest(request);

      if (response == null)
         {
         throw new IllegalStateException("O servidor do Oráculo retornou uma resposta vazia.");
         }
      if (response.getErrorMessage() != null)
         {
         throw new RuntimeException(response.getErrorMessage());
         }

      return (response.getExchangeRates());
      }

   private NetworkResponse sendRequest(NetworkRequest request) throws Exception
      {
      try (Socket socket = new Socket())
         {
         socket.connect(new InetSocketAddress(host, port), timeoutMillis);
         socket.setSoTimeout(timeoutMillis);
         try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream()); ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream()))
            {
            outputStream.writeObject(request);
            outputStream.flush();
            return ((NetworkResponse) inputStream.readObject());
            }
         }
      }

   private boolean loadCredentialsFromFile(Properties credentials)
      {
      try (InputStream input = new FileInputStream("oracle.env"))
         {
         credentials.load(input);
         return true;
         }
      catch (final FileNotFoundException exceptionValue)
         {
         log.warn("Arquivo oracle.env não encontrado para carregamento de credenciais");
         return false;
         }
      catch (final IOException exceptionValue)
         {
         log.error("Erro ao ler credenciais de oracle.env: {}", exceptionValue.getMessage(), exceptionValue);
         return false;
         }
      }

   /**
    * Obtém o host configurado para conexão ao Oráculo.
    *
    * @return o hostname ou IP do servidor
    */
   public String getHost()
      {
      return host;
      }

   /**
    * Obtém a porta TCP configurada do Oráculo.
    *
    * @return o número da porta
    */
   public int getPort()
      {
      return port;
      }

   /**
    * Obtém o nome do grupo utilizado para autenticação.
    *
    * @return o nome do grupo
    */
   public String getGroupName()
      {
      return groupName;
      }

   /**
    * Obtém a senha do grupo utilizada para autenticação.
    *
    * @return a senha do grupo
    */
   public String getGroupPassword()
      {
      return groupPassword;
      }
   }
