package org.unicamp.poo.oracle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import network.ExchangeRate;
import network.NetworkRequest;
import network.NetworkResponse;

public final class OracleSimpleClient
   {
   private static final Logger log = LoggerFactory.getLogger(OracleSimpleClient.class);

   private String host;
   private int    port;
   private String groupName;
   private String groupPassword;

   public OracleSimpleClient()
      {
      final Properties props = new Properties();
      final Properties credentials = new Properties();

      try (InputStream input = getClass().getClassLoader().getResourceAsStream("OracleServer.properties"))
         {
         if (input != null)
            {
            props.load(input);
            host = props.getProperty("serverIP");
            port = Integer.parseInt(props.getProperty("serverPort"));
            }

         groupName = System.getenv("ORACLE_GROUP_NAME");
         groupPassword = System.getenv("ORACLE_GROUP_PASSWORD");

         if ((groupName == null || groupPassword == null) && loadCredentialsFromFile(credentials))
            {
            if (groupName == null)
               {
               groupName = credentials.getProperty("ORACLE_GROUP_NAME");
               }
            if (groupPassword == null)
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

   public List<ExchangeRate> getValues(LocalDate since, LocalDate to) throws Exception
      {
      if (host == null || port <= 0)
         {
         throw new IllegalStateException("Servidor do Oráculo não configurado (host ou porta inválidos).");
         }
      if (groupName == null || groupPassword == null)
         {
         throw new IllegalStateException("Credenciais do Oráculo não configuradas.");
         }

      final NetworkRequest  request  = new NetworkRequest(groupName, groupPassword, since, to);
      final NetworkResponse response = sendRequest(request);

      if (response.errorMessage != null)
         {
         throw new RuntimeException(response.errorMessage);
         }

      return (response.getExchangeRates());
      }

   private NetworkResponse sendRequest(NetworkRequest request) throws Exception
      {
      try (Socket socket = new Socket(host, port); ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream()); ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream()))
         {
         outputStream.writeObject(request);
         outputStream.flush();
         return ((NetworkResponse) inputStream.readObject());
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

   public String getHost()
      {
      return host;
      }

   public int getPort()
      {
      return port;
      }

   public String getGroupName()
      {
      return groupName;
      }

   public String getGroupPassword()
      {
      return groupPassword;
      }
   }
