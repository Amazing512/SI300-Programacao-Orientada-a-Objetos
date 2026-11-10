# SI300 - Programação Orientada a Objetos

## Passo a passo: testes de integração

### MariaDB

1. Abra o PowerShell na pasta do projeto.
2. Configure as credenciais de uma base MariaDB de teste que tenha as tabelas da aplicação:

   ```powershell
   $env:MARIADB_TEST_URL = "jdbc:mariadb://localhost:3306/ftcoin_test"
   $env:MARIADB_TEST_USER = "seu_usuario"
   $env:MARIADB_TEST_PASSWORD = "sua_senha"
   ```

3. Execute o teste:

   ```powershell
   mvn "-Dmariadb.integration=true" "-Dtest=IntegrationTest.MariaDBIntegrationTest" test
   ```

### API do Oráculo

1. Abra o PowerShell na pasta do projeto.
2. Configure as credenciais do grupo:

   ```powershell
   $env:ORACLE_GROUP_NAME = "seu_grupo"
   $env:ORACLE_GROUP_PASSWORD = "sua_senha"
   ```

   Ou copie `oracle.env.example` para `oracle.env` e preencha as credenciais.
3. Execute o teste:

   ```powershell
   mvn "-Doracle.integration=true" "-Dtest=IntegrationTest.OracleApiIntegrationTest" test
   ```

### Executar os dois testes

1. Configure as variáveis de ambiente do MariaDB e do Oráculo conforme os passos acima.
2. Execute:

   ```powershell
   mvn "-Dmariadb.integration=true" "-Doracle.integration=true" "-Dtest=IntegrationTest.MariaDBIntegrationTest,IntegrationTest.OracleApiIntegrationTest" test
   ```
