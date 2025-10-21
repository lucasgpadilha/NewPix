---

# Projeto NewPix

Este projeto é um sistema bancário simplificado desenvolvido para a disciplina de Sistemas Distribuídos. Ele implementa uma arquitetura cliente-servidor usando Sockets Java, onde múltiplos clientes podem se conectar a um servidor central para realizar operações bancárias como login, cadastro, consulta de saldo, depósito e transferências (PIX).

## Pré-requisitos

Para executar o projeto, o avaliador precisará ter instalado:

* JDK 11 ou superior
* Eclipse IDE for Java Developers (que já inclui o Maven)
* Um servidor de banco de dados MySQL (Ex: XAMPP, WAMP, MySQL Community Server)

## 1. Configuração do Ambiente (Setup)

O projeto precisa de duas configurações para ser executado: a importação no Eclipse e a configuração do banco de dados.

### 1.1. Importar o Projeto no Eclipse

1.  Abra o Eclipse IDE.
2.  Vá em `File` > `Import...`.
3.  Na janela de importação, expanda a pasta `Maven`.
4.  Selecione **"Existing Maven Projects"** e clique em "Next".
5.  Clique em "Browse..." e navegue até a pasta raiz do projeto (a pasta que contém o arquivo `pom.xml`).
6.  O projeto `NewPix` deve aparecer na lista. Clique em "Finish".
7.  O Eclipse irá importar o projeto e baixar automaticamente as dependências (Jackson e MySQL Connector) definidas no `pom.xml`.

### 1.2. Banco de Dados

1.  Inicie seu serviço MySQL.
2.  Crie um novo banco de dados (schema) com o nome `newpix`.
3.  Importe o arquivo `newpix.sql`, localizado na raiz do projeto, para dentro do banco `newpix`. Isso criará as tabelas (`usuarios`, `sessoes`, `transacoes`) e inserirá alguns dados de teste.
4.  **Verifique as credenciais:** O projeto está configurado por padrão para se conectar como `root` e senha `""` (vazia). Se o seu MySQL tiver uma senha diferente, atualize o arquivo:
    `src/main/java/newpix/configs/DatabaseConfig.java`

## 2. Como Executar a Aplicação (Via Eclipse)

A ordem de execução é importante. O servidor deve ser iniciado primeiro.

### 2.1. Iniciar o Servidor

1.  No Eclipse, abra o "Package Explorer".
2.  Navegue até `src/main/java` > `newpix`.
3.  Encontre o arquivo `ServidorMain.java`.
4.  Clique com o **botão direito** em `ServidorMain.java`.
5.  Selecione **"Run As"** > **"1 Java Application"**.
6.  Uma janela pop-up (`JOptionPane`) aparecerá solicitando a porta. Digite a porta desejada (ex: `20000`) e clique em "OK".
7.  O painel principal do servidor (`ServerFrame`) será exibido, mostrando o status "Online".

### 2.2. Iniciar o Cliente

1.  No Eclipse, encontre o arquivo `ClienteMain.java` (na mesma pasta `newpix`).
2.  Clique com o **botão direito** em `ClienteMain.java`.
3.  Selecione **"Run As"** > **"1 Java Application"**.
4.  A tela de login (`AuthenticationFrame`) será exibida.
5.  Nos campos inferiores, preencha o endereço do servidor:
    * **IP:** `127.0.0.1` (se estiver executando na mesma máquina)
    * **Porta:** `20000` (ou a porta que você definiu ao iniciar o servidor)
6.  Agora você pode usar a tela para se cadastrar ou fazer login.

*(Para iniciar um segundo cliente, basta executar o passo 2.2 novamente.)*

## 3. Dados de Teste para Login

O arquivo `newpix.sql` já inclui usuários de teste. Você pode usar as seguintes credenciais para fazer login:

| Nome | CPF | Senha |
| :--- | :--- | :--- |
| Lucas Gonçalves Padilha | `123.123.123-00` | `123123` |
| Gabrielly | `123.123.123-01` | `123123` |
| Sebastiao | `123.123.123-02` | `123123` |