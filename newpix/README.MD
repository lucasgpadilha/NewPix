-----

# Projeto NewPix

Este projeto é um sistema bancário simplificado desenvolvido para a disciplina de Sistemas Distribuídos. Ele implementa uma arquitetura cliente-servidor usando Sockets Java, onde múltiplos clientes podem se conectar a um servidor central para realizar operações bancárias como login, cadastro, consulta de saldo, depósito e transferências (PIX).

## Tecnologias Utilizadas

  * **Linguagem:** Java 11
  * **Build:** Apache Maven
  * **Banco de Dados:** MySQL
  * **Interface Gráfica (Cliente):** Java Swing
  * **Comunicação:** Sockets TCP/IP
  * **Serialização:** Jackson (JSON)

## Pré-requisitos

Para compilar e executar o projeto, você precisará ter instalado:

  * JDK 11 ou superior
  * Apache Maven
  * Um servidor de banco de dados MySQL (Ex: XAMPP, WAMP, MySQL Community Server)

## 1\. Configuração do Ambiente (Setup)

Antes de executar, o banco de dados precisa ser configurado.

### 1.1. Banco de Dados

1.  Inicie seu serviço MySQL.
2.  Crie um novo banco de dados (schema) com o nome `newpix`.
3.  Importe o arquivo `newpix.sql`, localizado na raiz do projeto, para dentro do banco `newpix`. Isso criará as tabelas (`usuarios`, `sessoes`, `transacoes`) e inserirá alguns dados de teste.
4.  **Verifique as credenciais:** O projeto está configurado por padrão para se conectar como `root` e senha `""` (vazia). Se o seu MySQL tiver uma senha diferente, atualize o arquivo:
    `src/main/java/newpix/configs/DatabaseConfig.java`

### 1.2. Compilando o Projeto

O projeto utiliza o Maven para gerenciar dependências e gerar os arquivos JAR executáveis.

1.  Abra um terminal na pasta raiz do projeto (onde o arquivo `pom.xml` está localizado).

2.  Execute o seguinte comando Maven:

    ```bash
    mvn clean package
    ```

3.  Após a conclusão, o Maven terá criado dois arquivos executáveis na pasta `target/`:

      * `servidor.jar`
      * `cliente.jar`

## 2\. Como Executar a Aplicação

A ordem de execução é importante. O servidor deve ser iniciado primeiro.

### 2.1. Iniciar o Servidor

1.  No terminal, navegue até a pasta `target/`:

    ```bash
    cd target
    ```

2.  Execute o JAR do servidor:

    ```bash
    java -jar servidor.jar
    ```

3.  Uma janela pop-up (`JOptionPane`) aparecerá solicitando a porta na qual o servidor deve escutar.

4.  Digite a porta desejada (ex: `20000`) e clique em "OK".

5.  O painel principal do servidor (`ServerFrame`) será exibido, mostrando o status "Online" e os logs de conexão.

### 2.2. Iniciar o Cliente

1.  Abra um **novo terminal** (mantenha o terminal do servidor em execução).

2.  Navegue até a pasta `target/`:

    ```bash
    cd target
    ```

3.  Execute o JAR do cliente:

    ```bash
    java -jar cliente.jar
    ```

4.  A tela de login (`AuthenticationFrame`) será exibida.

5.  Nos campos inferiores, preencha o endereço do servidor:

      * **IP:** `127.0.0.1` (se estiver executando na mesma máquina)
      * **Porta:** `20000` (ou a porta que você definiu ao iniciar o servidor)

6.  Agora você pode usar a tela para se cadastrar ou fazer login.

## 3\. Dados de Teste para Login

O arquivo `newpix.sql` já inclui usuários de teste. Você pode usar as seguintes credenciais para fazer login:

| Nome | CPF | Senha |
| :--- | :--- | :--- |
| Lucas Gonçalves Padilha | `123.123.123-00` | `123123` |
| Gabrielly | `123.123.123-01` | `123123` |
| Sebastiao | `123.123.123-02` | `123123` |