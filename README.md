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

## 📋 Status Atual do Projeto (Pronto para Entrega)

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

## 🧪 Fluxo de Testes Passo a Passo (Com Auto-Deploy)

No diretório [testes.http](file:///C:/Users/luzin/IdeaProjects/Projeto-consorcio/src/testes.http), você encontrará requisições prontas para executar esse fluxo completo diretamente na sua IDE:

### Passo 1: Cadastrar os Usuários de Teste
Crie pelo menos dois participantes no banco de dados.
> [!IMPORTANT]
> Em `carteiraWeb3`, utilize os endereços públicos de contas geradas pelo seu Ganache (por exemplo, a Conta 2 e a Conta 3, visto que a Conta 1 é usada pelo backend/admin).
* Dispare a requisição `POST /usuarios` para cada participante.

### Passo 2: Criar o Grupo (Auto-Deploy Encadeado)
Envie a requisição `POST /grupos/criar` **removendo ou deixando em branco** o campo `enderecoContrato`.
* **O que acontece**: O Java percebe que a blockchain local está ativa mas o grupo não tem contrato. Ele realiza o deploy automático da moeda (`MockToken`) e depois do consórcio (`ConsortiumGroup`) no Ganache.
* Você verá os novos endereços impressos no console do Spring Boot e salvos no banco.

### Passo 3: Adquirir as Cotas
Adicione os usuários criados no grupo que foi gerado no Passo 2.
* Chame `POST /cotas/comprar` passando o `usuarioId` e o `grupoId`. A carteira do usuário será registrada no contrato inteligente correspondente ao grupo.

### Passo 4: Iniciar o Grupo
Mude o status do grupo para ativo, o que irá gerar e programar o carnê de parcelas.
* Chame `PATCH /grupos/iniciar/{id}`.

### Passo 5: Executar Sorteio ou Contemplação por Lance
Agora você pode simular a assembleia da rodada atual:
* **Sorteio**: Chame `POST /assembleia/grupos/{id}/sortear` para contemplar um participante por sorteio.
* **Lance**: Chame `POST /assembleia/grupos/{id}/contemplar-lance?tipoLance=LIVRE` para eleger e contemplar o vencedor da rodada de lances.

### Passo 6: Consultar o Caixa do Consórcio
Consulte em tempo real quanto de saldo o grupo possui e o valor da carta de crédito consultando diretamente os dados na Blockchain:
* Chame `GET /grupos/{id}/caixa`.

