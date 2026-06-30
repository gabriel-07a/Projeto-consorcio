# Sistema de Consórcio Descentralizado (Web3 + Spring Boot)

Este projeto consiste em um sistema de gerenciamento de grupos de consórcio integrado à rede blockchain Ethereum (via Web3j). O sistema permite o gerenciamento completo do ciclo de vida dos grupos, adesão de participantes, controle de parcelas, registro de lances, e a realização de assembleias de contemplação (via sorteio ou diferentes modalidades de lance).

---

## 🚀 Arquitetura e Tecnologias

- **Backend**: Java 21 / 23 & Spring Boot 4.0.6
- **Banco de Dados**: MySQL (JPA / Hibernate)
- **Integração Web3**: Web3j (interagindo com Smart Contracts em Solidity)
- **Blockchain de Testes**: Ganache (RPC URL: `http://127.0.0.1:7545`)
- **Automação de Build**: Maven

---

## 📋 Status do Projeto 

O projeto está **completamente funcional e estruturado**. Todas as regras de negócio críticas foram implementadas, validadas e testadas:

1. **Gestão de Grupos e Cotas com Deploy Automático**:
   - Criação de grupos e validação de limite de participantes.
   - **Deploy Automático de Contratos**: Ao criar um grupo (`POST /grupos/criar`), caso o campo `enderecoContrato` não seja fornecido (ou venha em branco), o backend do Spring Boot realiza automaticamente o deploy do Smart Contract `ConsortiumGroup` na Blockchain (Ganache).
   - **Deploy Automático de Token (USDT)**: Caso a propriedade de stablecoin no `application.properties` esteja em branco ou zerada, o backend realiza primeiro o deploy automático do seu contrato [MockToken.sol](file:///C:/Users/luzin/IdeaProjects/Projeto-consorcio/src/main/resources/solidity/MockToken.sol) na blockchain e utiliza o endereço desse token recém-criado como moeda oficial no contrato de consórcio!
   - Fluxo de status do grupo (`EM_FORMACAO` -> `EM_ANDAMENTO` -> `ENCERRADO`).
   - Aquisição de cotas integrada com registro na blockchain.
   - Cancelamento lógico de cotas (Soft Delete).

2. **Faturamento e Pagamentos (Parcelas)**:
   - Geração automática do carnê de parcelas ao iniciar o grupo.
   - Rotina automática de fiscalização de inadimplência (`@Scheduled`), aplicando multas e marcando restrições financeiras na blockchain para parcelas atrasadas.
   - Confirmação de pagamentos com escuta ativa de eventos de transações da blockchain.

3. **Estratégias de Contemplação (Bidding Strategies)**:
   - **Sorteio**: Escolhe aleatoriamente uma cota ativa e sem pendências.
   - **Lance Livre**: Elege o lance de maior valor no ciclo.
   - **Lance Fixo**: Elege um lance por sorteio entre aqueles que ofertaram o valor fixado.
   - **Lance Embutido**: Utiliza parte da carta de crédito como lance e elege o maior valor ofertado.
   - *Critério de Desempate*: Implementado critério de prioridade cronológica (o participante que ofertou primeiro/menor ID de transação vence o empate).

4. **Escuta Ativa de Eventos (Blockchain Listeners)**:
   - O `BlockchainEventListener` escuta de forma assíncrona os eventos `installmentPaidEventFlowable` (pagamento de parcela) e `bidPlacedEventFlowable` (registro de lances) diretamente da rede blockchain e sincroniza automaticamente com o banco de dados MySQL local.


---

## 🛠️ Como Configurar e Executar o Projeto

Para executar o projeto com sucesso para a apresentação/entrega, siga os seguintes passos:

### 1. Pré-requisitos
- Java JDK 21 ou superior instalado.
- Servidor MySQL rodando localmente (pode ser via Docker ou instalação nativa).
- Ganache instalado e executando (RPC Server: `http://127.0.0.1:7545`).

### 2. Configurando o Banco de Dados (MySQL)
No arquivo `src/main/resources/application.properties`, configure as credenciais do seu banco de dados:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/consorcio_db?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### 3. Configurando a Rede Blockchain (Ganache)
1. No Ganache, crie um **Workspace** apontando para a porta `7545`.
2. Copie o endereço privado (Private Key) de uma das contas do Ganache (que atuará como a carteira administradora do sistema) e adicione no `application.properties`:
```properties
web3.rpc-url=http://127.0.0.1:7545
web3.backend-private-key=0xSUA_CHAVE_PRIVADA_AQUI
```

### 4. Executando o Projeto
Abra o terminal no diretório raiz do projeto e execute o comando:
```bash
./mvnw spring-boot:run
```
O servidor Spring Boot iniciará por padrão na porta `8080`.


---

## 🧪 Fluxo de Testes Passo a Passo (Deploy Manual via Remix)

No diretório `testes.http`, você encontrará requisições prontas para executar esse fluxo completo diretamente na sua IDE. Como a arquitetura exige a presença física dos contratos na blockchain antes da criação do grupo, faremos o deploy via **Remix IDE**.

### Passo 1: Cadastrar os Usuários de Teste (Backend)
Crie pelo menos dois participantes no banco de dados através da sua API.
> [!IMPORTANT]
> Em `carteiraWeb3`, utilize os endereços públicos de contas geradas pelo seu Ganache (por exemplo, a Conta 2 e a Conta 3, visto que a Conta 1 é usada pelo backend/admin).
* Dispare a requisição `POST /usuarios` para cada participante.

### Passo 2: Deploy dos Contratos Inteligentes via Remix IDE
Como o sistema interage com tokens ERC20 reais (ou simulados), precisamos primeiro lançar a moeda na rede local e, em seguida, lançar o contrato do consórcio informando qual moeda ele deve aceitar.

#### 2.1 - Configurar o Ambiente no Remix
1. Abra o [Remix IDE](https://remix.ethereum.org/) no navegador.
2. Na aba **Deploy & Run Transactions**, altere o campo **Environment** para `Dev - Ganache Provider` ou `Custom - External Http Provider` e aponte para `http://127.0.0.1:7545`.
3. Verifique se as contas do seu Ganache apareceram no campo **Account**. Selecione a primeira conta (Conta Admin).

#### 2.2 - Deploy da Moeda (MockToken)
1. Crie um arquivo `MockToken.sol` no Remix e cole o código do seu token USDT simulado.
2. Compile o contrato na aba **Solidity Compiler** (versão `0.8.24`).
3. Vá para **Deploy**, selecione o `MockToken` e clique em **Deploy**.
4. **Ação Crucial:** Copie o endereço do contrato do token gerado (ex: `0x123...`). Cole este endereço no seu arquivo `application.properties` do Spring Boot (na variável que define o endereço da stablecoin) e reserve-o para o próximo passo.

#### 2.3 - Deploy do Consórcio (ConsortiumGroup)
1. Crie um arquivo `ConsortiumGroup.sol`, cole o código do consórcio e compile. O Remix baixará automaticamente as bibliotecas da **OpenZeppelin**.
2. Na aba **Deploy**, expanda a setinha ao lado do botão de Deploy para preencher os parâmetros do `constructor`:
   * **admin**: Endereço da Conta 1 do Ganache.
   * **backend**: Endereço da Conta 1 do Ganache (a mesma que assinará as transações do Spring Boot).
   * **stablecoinAddress**: Cole o endereço do `MockToken` gerado no passo anterior.
   * **_creditValue**: O valor do prêmio em Wei. Ex: para 50.000, digite `50000000000000000000000` (50 mil + 18 zeros).
   * **_installmentValue**: O valor da parcela em Wei. Ex: `1000000000000000000000` (1 mil + 18 zeros).
   * **_totalMonths**: A duração. Ex: `50`.
   * **_maxContemplationsPerCycle**: Limite de contemplações por mês. Ex: `2`.
3. Clique em **Transact**.
4. Copie o endereço gerado para o contrato `ConsortiumGroup`.

### Passo 3: Criar o Grupo no Banco de Dados
Com o contrato vivo na blockchain, vamos espelhá-lo no sistema relacional.
* Envie a requisição `POST /grupos/criar`.
* **Obrigatório:** Preencha o campo `enderecoContrato` no JSON com o endereço do `ConsortiumGroup` que você acabou de copiar do Remix.

### Passo 4: Adquirir as Cotas
Adicione os usuários criados no Passo 1 ao grupo gerado no Passo 3.
* Chame `POST /cotas/comprar` passando o `usuarioId` e o `grupoId`. A API registrará a carteira do usuário no contrato inteligente utilizando o `BACKEND_ROLE`.

### Passo 5: Iniciar o Grupo e Realizar Pagamentos
Mude o status do grupo para ativo, o que irá gerar e programar o carnê de parcelas.
* Chame `PATCH /grupos/iniciar/{id}`.
* Para simular o pagamento, você pode chamar diretamente a função `payInstallment()` no Remix usando a conta do usuário (selecionando a carteira do cliente no topo do Remix), ou via endpoint da API se você construiu uma rota para assinar a transação.

### Passo 6: Executar Sorteio ou Contemplação por Lance
Agora você pode simular a assembleia da rodada atual via Spring Boot:
* **Sorteio**: Chame `POST /assembleia/grupos/{id}/sortear` para contemplar um participante por sorteio.
* **Lance**: Chame `POST /assembleia/grupos/{id}/contemplar-lance?tipoLance=LIVRE` para eleger e contemplar o vencedor da rodada.
  *(O backend validará os saldos, alterará os status na blockchain e o Smart Contract transferirá o prêmio automaticamente).*

### Passo 7: Consultar o Caixa do Consórcio
Consulte em tempo real quanto de saldo o grupo possui e o valor da carta de crédito verificando os dados puros da rede Web3:
* Chame `GET /grupos/{id}/caixa`.
