// SPDX-License-Identifier: MIT
// Define a versão do compilador Solidity. O símbolo ^ garante compatibilidade com versões 0.8.24 até antes da 0.9.0.
pragma solidity ^0.8.24;

// =====================================================================================================
// IMPORTAÇÕES DA OPENZEPPELIN (O Padrão de Ouro de Segurança da Indústria Web3)
// =====================================================================================================
import "./openzeppelin/contracts/access/AccessControl.sol"; // Sistema de permissões baseado em papéis (Roles)
import "./openzeppelin/contracts/utils/ReentrancyGuard.sol"; // Proteção contra ataques de reentrada (hacks de levantamento infinito)
import "./openzeppelin/contracts/token/ERC20/utils/SafeERC20.sol"; // Garante que transferências de tokens (USDT/USDC) não falham silenciosamente
import "./openzeppelin/contracts/utils/Pausable.sol"; // Mecanismo de emergência para congelar o contrato se algo correr mal

// O contrato herda (is) as três bibliotecas para absorver as suas funções de segurança e controle.
contract ConsortiumGroup is AccessControl, ReentrancyGuard, Pausable {
    // Vincula a biblioteca SafeERC20 à interface IERC20. Isto ativa funções seguras como safeTransfer e safeTransferFrom.
    using SafeERC20 for IERC20;

    // =====================================================================================================
    // 1. ENUMS (Estruturas de Opções Fixas)
    // =====================================================================================================
    // Define como um participante foi contemplado. O Java lerá isto para saber se foi por sorteio ou por maior lance.
    enum ContemplationType { DRAW, BID }

    // [Melhoria 4] Tipos de lances permitidos no mercado real de consórcios.
    // FREE = Lance Livre, FIXED = Lance Fixo (ex: 30% do grupo), EMBEDDED = Lance Embutido (usa parte do próprio prémio).
    enum BidType { FREE, FIXED, EMBEDDED }

    // =====================================================================================================
    // 2. STRUCTS (Modelos de Dados / Tabelas do Contrato)
    // =====================================================================================================
    // [Melhoria 5] Guarda dados detalhados sobre a dívida do cliente. O teu Java (Oráculo) alimentará estes campos.
    struct DebtInfo {
        uint256 overdueInstallments; // Contador de quantas mensalidades o utilizador tem em atraso neste momento.
        uint256 accumulatedPenalty;  // Valor total acumulado de multas/juros em formato de token (calculado pelo Java).
    }

    // Ficha cadastral do participante dentro da Blockchain.
    struct Participant {
        bool registered;           // True se a carteira foi registada pela API Java. Impedirá registos duplicados.
        bool contemplated;         // True assim que ganhar o prémio. Bloqueia o utilizador para nunca mais ganhar neste grupo.
        bool active;               // Permite desativar um utilizador (ex: desistência ou exclusão judicial).
        uint256 installmentsPaid;  // Contador de quantas parcelas mensais o utilizador já pagou com sucesso na rede.
        DebtInfo debt;             // Ligação com a estrutura de dívidas explicada acima.
        uint256 totalBidAmount;    // O saldo de dinheiro que este utilizador tem depositado no cofre de lances do mês atual.
    }

    // [Melhoria 3] Modelo para o livro-razão histórico. Cada contemplação gera um registo imutável para auditoria.
    struct Contemplation {
        address winner;                     // Endereço da carteira Web3 do vencedor.
        uint256 cycle;                      // O mês/ciclo em que ele venceu.
        uint256 amount;                     // O valor do prémio entregue (Ex: 50.000).
        uint256 timestamp;                  // O segundo exato do relógio Unix em que a transação foi gravada na rede.
        ContemplationType contemplationType; // Se foi por DRAW (Sorteio) ou BID (Lance).
    }

    // Registador de histórico de lances. O Java consultará isto para auditar quem tentou ganhar em cada mês.
    struct Bid {
        uint256 cycle;      // Mês do lance.
        uint256 amount;     // Valor ofertado.
        BidType bidType;    // Estratégia do lance.
        bool winner;        // Se este lance específico foi o vencedor da assembleia.
    }

    // =====================================================================================================
    // 3. MAPEAMENTOS (Os "HashMaps" ou Tabelas de Base de Dados On-Chain)
    // =====================================================================================================
    // Liga o endereço de uma carteira Web3 diretamente à ficha do Participant. (Ex: 0x123... => Ficha do Bob)
    mapping(address => Participant) public participants;

    // Liga a carteira do utilizador + o número do ciclo ao Lance correspondente. Permite guardar o histórico mês a mês.
    mapping(address => mapping(uint256 => Bid)) public bidsByCycle;

    // Array dinâmico que funciona como uma lista pública e imutável de todas as contemplações da história do contrato.
    Contemplation[] public contemplations;

    // =====================================================================================================
    // 4. PAPÉIS E PERMISSÕES (AccessControl)
    // =====================================================================================================
    // Cria o identificador único para o cargo do teu servidor Spring Boot através de um hash criptográfico.
    bytes32 public constant BACKEND_ROLE = keccak256("BACKEND_ROLE");

    // =====================================================================================================
    // 5. VARIÁVEIS DE ESTADO (Constantes Imutáveis e Contadores Financeiros)
    // =====================================================================================================
    IERC20 public immutable stablecoin;       // Endereço do contrato do token que o grupo aceita (Ex: USDC ou USDT).
    uint256 public immutable creditValue;      // Valor total do prémio de consórcio (Ex: 50000000000000000000000 se tiver 18 casas decimais).
    uint256 public immutable installmentValue; // Valor exato da mensalidade fixa que o contrato exige.
    uint256 public immutable totalMonths;      // Duração total do grupo (Ex: 60 meses).

    // [Melhoria 1] SEPARAÇÃO CONTÁBIL ESTRITA DE FUNDOS (Evita contaminação de caixa e roubo de lances)
    uint256 public consortiumFundBalance; // Saldo real do grupo: composto apenas por mensalidades pagas e lances de quem ganhou.
    uint256 public bidFundBalance;        // Saldo volátil de reserva: composto por lances de quem está a competir e pode resgatar.

    // [Melhoria 2] MÁQUINA DE ESTADOS DO CALENDÁRIO
    uint256 public currentCycle;             // O mês atual em que o consórcio se encontra (Começa em 1).
    mapping(uint256 => bool) public cycleClosed; // Mapeia se as assembleias e lances daquele mês específico já foram encerrados.

    // [Melhoria 7] CONTROLE DE ENCERRAMENTO DO GRUPO
    bool public finished;             // Vira 'true' quando o consórcio termina e bloqueia todas as funções financeiras para sempre.
    uint256 public contemplatedCount; // Contador de quantas pessoas já foram contempladas no total do grupo.

    // [Melhoria 8] LIMITE DE SEGURANÇA CONTRA SAQUES EM MASSA
    uint256 public maxContemplationsPerCycle;            // Máximo de prémios que o Java pode autorizar por mês (Ex: no máximo 2).
    mapping(uint256 => uint256) public cycleContemplations; // Conta quantos prémios já foram pagos dentro do ciclo atual.

    // =====================================================================================================
    // 6. EVENTOS (Os "Webhooks" ou Logs de Auditoria da Blockchain)
    // =====================================================================================================
    // O teu Spring Boot terá uma classe "Listener" focada em ouvir estes eventos em tempo real para atualizar o teu banco SQL local.
    event ParticipantRegistered(address indexed participant);
    event InstallmentPaid(address indexed participant, uint256 amount, uint256 indexed cycle);
    event BidPlaced(address indexed participant, uint256 amount, uint256 indexed cycle, BidType bidType);
    event BidWithdrawn(address indexed participant, uint256 amount, uint256 indexed cycle);
    event ParticipantContemplated(address indexed winner, uint256 amount, uint256 indexed cycle, ContemplationType cType);
    event CycleOpened(uint256 indexed cycle);
    event CycleClosed(uint256 indexed cycle);
    event ParticipantDelinquent(address indexed participant, uint256 overdueInstallments, uint256 accumulatedPenalty);
    event ParticipantRegularized(address indexed participant, uint256 penaltyPaid);
    event ConsortiumFinished();

    // =====================================================================================================
    // 7. CONSTRUTOR (Executado uma única vez durante o Deploy)
    // =====================================================================================================
    constructor(
        address admin,            // Carteira do dono da empresa (gerencia pausas e configurações).
        address backend,          // Carteira da API Java (assina transações automáticas de rotina).
        address stablecoinAddress, // Endereço do token estável (Ex: USDT).
        uint256 _creditValue,      // Valor do prémio do consórcio.
        uint256 _installmentValue, // Valor da parcela mensal.
        uint256 _totalMonths,      // Total de meses de duração.
        uint256 _maxContemplationsPerCycle // Limite mensal de contemplações.
    ) {
        stablecoin = IERC20(stablecoinAddress);
        creditValue = _creditValue;
        installmentValue = _installmentValue;
        totalMonths = _totalMonths;
        maxContemplationsPerCycle = _maxContemplationsPerCycle;

        // Atribuição de permissões imutáveis da OpenZeppelin
        _grantRole(DEFAULT_ADMIN_ROLE, admin); // Define quem manda no contrato
        _grantRole(BACKEND_ROLE, backend);     // Define que a carteira do Java tem acesso administrativo técnico

        currentCycle = 1; // O consórcio nasce oficialmente no Mês 1
        emit CycleOpened(1); // Dispara o log na rede avisando que o Mês 1 está aberto para negócios
    }

    // =====================================================================================================
    // 8. CONTROLO DE CICLOS MENSAIS (O Java comanda com base no calendário do servidor)
    // =====================================================================================================
    // Abre as portas para o novo mês. Impede lances ou pagamentos fora do período correto.
    function openCycle(uint256 cycle) external onlyRole(BACKEND_ROLE) {
        require(!finished, "Consorcio encerrado");
        require(cycle == currentCycle + 1, "Ciclos devem ser sequenciais");
        require(cycleClosed[currentCycle], "Feche o ciclo atual antes de abrir o proximo");

        currentCycle = cycle;
        emit CycleOpened(cycle); // O Java ouve isto e envia notificações por email para os clientes avisando: "Novo mês aberto!"
    }

    // Tranca os lances do mês corrente. O Java chama isto no segundo exato em que a janela de lances da assembleia fecha.
    function closeCycle(uint256 cycle) external onlyRole(BACKEND_ROLE) {
        require(cycle == currentCycle, "Apenas o ciclo atual pode ser fechado");
        require(!cycleClosed[cycle], "Ciclo ja esta fechado");

        cycleClosed[cycle] = true;
        emit CycleClosed(cycle);
    }

    // =====================================================================================================
    // 9. FUNÇÕES DE ORÁCULO E CADASTRO (Apenas a API Java pode executar)
    // =====================================================================================================
    // Regista um novo cliente aprovado no sistema tradicional para dentro do ecossistema Web3.
    function registerParticipant(address participant) external onlyRole(BACKEND_ROLE) {
        require(!finished, "Consorcio encerrado");
        require(!participants[participant].registered, "O utilizador ja esta registado");

        participants[participant].registered = true;
        participants[participant].active = true;

        emit ParticipantRegistered(participant);
    }

    // [Melhoria 5] Funciona como um "Carimbo Eletrónico". Se o cliente atrasar o pagamento no boleto/PIX tradicional,
    // o Java chama esta função para bloquear o acesso dele a sorteios na Blockchain e aplicar a multa calculada.
    function markDelinquent(address participant, uint256 penaltyAmount) external onlyRole(BACKEND_ROLE) {
        Participant storage p = participants[participant];
        require(p.registered, "Utilizador nao registrado");

        p.debt.overdueInstallments++;
        p.debt.accumulatedPenalty += penaltyAmount; // Adiciona a multa de 3% ou juros calculados pelo motor de faturas do Java

        emit ParticipantDelinquent(participant, p.debt.overdueInstallments, p.debt.accumulatedPenalty);
    }

    // Remove as penalidades e desbloqueia o utilizador assim que o Java deteta a liquidação das faturas atrasadas.
    function regularizeParticipant(address participant) external onlyRole(BACKEND_ROLE) {
        Participant storage p = participants[participant];
        require(p.registered, "Utilizador nao registrado");

        p.debt.overdueInstallments = 0;
        uint256 penaltyPaid = p.debt.accumulatedPenalty;
        p.debt.accumulatedPenalty = 0; // Zera a dívida na blockchain

        emit ParticipantRegularized(participant, penaltyPaid); // O Java ouve e atualiza o histórico financeiro do cliente no painel Web2
    }

    // =====================================================================================================
    // 10. OPERAÇÕES FINANCEIRAS DOS UTILIZADORES (O Cliente aciona via MetaMask/Front-end)
    // =====================================================================================================
    // Executa a cobrança da mensalidade diretamente em Stablecoins.
    function payInstallment() external nonReentrant whenNotPaused {
        require(!finished, "Consorcio encerrado");
        Participant storage p = participants[msg.sender]; // Captura a ficha de quem assinou a transação (msg.sender)

        // Travas de Segurança Cruciais
        require(p.registered, "Utilizador nao registado");
        require(p.installmentsPaid < totalMonths, "Todas as mensalidades ja foram pagas");
        require(p.debt.overdueInstallments == 0, "Regularize suas parcelas atrasadas com a administracao");

        // [MÁGICA DA WEB3]: Puxa o dinheiro da MetaMask do utilizador e envia para o endereço deste contrato (address(this))
        stablecoin.safeTransferFrom(msg.sender, address(this), installmentValue);

        // [Melhoria 1]: Aloca o dinheiro estritamente no cofre do consórcio (dinheiro protegido que pagará os prémios)
        consortiumFundBalance += installmentValue;

        p.installmentsPaid++; // Incrementa o histórico de adimplência do cliente

        emit InstallmentPaid(msg.sender, installmentValue, currentCycle);
    }

    // Deposita uma quantia para competir pelo maior lance da assembleia do mês.
    function placeBid(uint256 amount, BidType bidType) external nonReentrant whenNotPaused {
        require(!finished, "Consorcio encerrado");
        require(!cycleClosed[currentCycle], "Lances encerrados para o ciclo atual");

        Participant storage p = participants[msg.sender];
        require(p.registered, "Utilizador nao registado");
        require(!p.contemplated, "Utilizadores contemplados nao podem dar lances");
        require(amount > 0, "O lance deve ser maior que zero");

        // Puxa as moedas do lance da carteira do utilizador
        stablecoin.safeTransferFrom(msg.sender, address(this), amount);

        // [Melhoria 1]: Joga o dinheiro no cofre temporário. Este saldo NÃO PODE ser usado para pagar prémios ainda,
        // porque se este utilizador perder, ele tem o direito de o reaver.
        bidFundBalance += amount;
        p.totalBidAmount += amount; // Acumula o saldo de lances do cliente

        // Grava na Blockchain o histórico desta tentativa de lance para auditoria eterna do grupo
        bidsByCycle[msg.sender][currentCycle] = Bid({
            cycle: currentCycle,
            amount: p.totalBidAmount,
            bidType: bidType,
            winner: false
        });

        emit BidPlaced(msg.sender, amount, currentCycle, bidType);
    }

    // Função autónoma: se o utilizador perceber pelo painel que perdeu o lance do mês, ele próprio clica num botão
    // e o contrato devolve o dinheiro imediatamente, sem passar por aprovação humana da empresa.
    function withdrawBid() external nonReentrant {
        Participant storage p = participants[msg.sender];
        uint256 amount = p.totalBidAmount;

        // Travas de proteção antibug/antifraude
        require(amount > 0, "Nenhum lance para resgatar");
        require(!p.contemplated, "Vencedores nao podem retirar o lance");
        require(!bidsByCycle[msg.sender][currentCycle].winner, "Lances vencedores ficam retidos no caixa");

        p.totalBidAmount = 0; // ZERA o saldo ANTES da transferência real (Elimina 100% o risco de ataque de reentrada)

        // Deduz o valor do cofre temporário de lances livres e devolve ao dono original
        bidFundBalance -= amount;
        stablecoin.safeTransfer(msg.sender, amount);

        emit BidWithdrawn(msg.sender, amount, currentCycle);
    }

    // =====================================================================================================
    // 11. O MOTOR DE ELEGIBILIDADE E ENTREGA DO PRÉMIO (O Clímax da Arquitetura)
    // =====================================================================================================
    // Função pública do tipo view (não gasta gás para ler). O teu Java chama isto antes de rodar o sorteio
    // para limpar a lista de clientes do banco de dados e garantir que só sorteia quem está 100% limpo na blockchain.
    function isEligible(address participant) public view returns (bool) {
        Participant storage p = participants[participant];
        return p.registered &&
               p.active &&
               !p.contemplated &&
               p.debt.overdueInstallments == 0 &&
               !finished;
    }

    // Função executada exclusivamente pelo Java para pagar o prémio (Ex: 50.000 USDT) direto na carteira do vencedor.
    function contemplateWinner(address winner, ContemplationType cType) external onlyRole(BACKEND_ROLE) nonReentrant {
        require(!finished, "Consorcio encerrado");
        require(isEligible(winner), "O utilizador nao e elegivel para vencer");

        // [Melhoria 8]: Verifica se o Java não ultrapassou o limite máximo de saídas configurado para este mês
        require(cycleContemplations[currentCycle] < maxContemplationsPerCycle, "Limite de contemplacoes do ciclo atingido");

        // [Melhoria 1]: SEGURANÇA MÁXIMA DO COFRE - O contrato valida matematicamente se o fundo do consórcio
        // possui dinheiro real suficiente para pagar o prémio. Evita insolvência ou bugs de saldo negativo.
        require(consortiumFundBalance >= creditValue, "Fundos insuficientes no cofre do consorcio");

        Participant storage p = participants[winner];
        p.contemplated = true; // Tranca a ficha do cliente. Ele nunca mais poderá ser introduzido nesta função.

        contemplatedCount++; // Sobe as métricas gerais do contrato
        cycleContemplations[currentCycle]++; // Consome 1 vaga do limite de pagamentos do mês atual

        // [MÁGICA DA BOLA DE NEVE]: Se este cliente foi o vencedor por Lance (BID):
        if (cType == ContemplationType.BID) {
            uint256 winningBid = p.totalBidAmount;
            require(winningBid > 0, "Participante nao possui lance ativo para este ciclo");

            // Marca o lance deste mês como o campeão imutável
            bidsByCycle[winner][currentCycle].winner = true;

            // O dinheiro do lance dele deixa de ser "resgatável" (sai do cofre temporário)
            // e converte-se em dinheiro estável do consórcio (entra para o fundo permanente).
            bidFundBalance -= winningBid;
            consortiumFundBalance += winningBid;

            p.totalBidAmount = 0; // Neutraliza o saldo ativo de lances dele para que ele nunca consiga usar o 'withdrawBid'
        }

        // Deduz o prémio pago do caixa real do consórcio
        consortiumFundBalance -= creditValue;

        // [Melhoria 3]: Alimenta o livro-razão histórico interno da Blockchain
        contemplations.push(Contemplation({
            winner: winner,
            cycle: currentCycle,
            amount: creditValue,
            timestamp: block.timestamp,
            contemplationType: cType
        }));

        // ENVIAR O DINHEIRO DO PRÉMIO: Transfere os tokens diretamente do contrato para a carteira do vencedor!
        stablecoin.safeTransfer(winner, creditValue);

        emit ParticipantContemplated(winner, creditValue, currentCycle, cType);
    }

    // =====================================================================================================
    // 12. GOVERNANÇA, SEGURANÇA ADMINISTRATIVA E RECUPERAÇÃO DE ERROS
    // =====================================================================================================
    // Permite que a gerência altere o limite de prémios mensais se o caixa do consórcio crescer muito rápido.
    function setMaxContemplationsPerCycle(uint256 _max) external onlyRole(DEFAULT_ADMIN_ROLE) {
        maxContemplationsPerCycle = _max;
    }

    // Acionador manual para encerrar o grupo de consórcio quando todas as cotas e meses chegarem ao fim.
    function forceFinishConsortium() external onlyRole(DEFAULT_ADMIN_ROLE) {
        finished = true;
        emit ConsortiumFinished();
    }

    // [Melhoria 10]: SALVA-VIDAS DE TOKENS. Se um cliente cometer um erro absurdo e enviar tokens errados
    // (ex: enviar LINK ou MATIC direto para o endereço do contrato por engano), o Admin consegue resgatar esses tokens
    // e devolver ao cliente. A linha 'require' impede que o Admin roube os USDT/USDC estáveis do consórcio.
    function rescueAccidentalERC20(address tokenAddress, uint256 amount) external onlyRole(DEFAULT_ADMIN_ROLE) {
        require(tokenAddress != address(stablecoin), "Nao e permitido retirar o token principal do consorcio");
        IERC20(tokenAddress).safeTransfer(msg.sender, amount);
    }

    // Funções de pânico herdadas da biblioteca Pausable da OpenZeppelin.
    // Em caso de suspeita de falha no servidor ou tentativa de hack, o Admin gela o contrato instantaneamente.
    function pause() external onlyRole(DEFAULT_ADMIN_ROLE) { _pause(); }

    // Libera os botões de pagamento e lances assim que o perigo passar ou a manutenção do servidor Java terminar.
    function unpause() external onlyRole(DEFAULT_ADMIN_ROLE) { _unpause(); }
}