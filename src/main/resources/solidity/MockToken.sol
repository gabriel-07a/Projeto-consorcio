// SPDX-License-Identifier: MIT
pragma solidity ^0.8.24;
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

contract MockToken is ERC20 {
    constructor() ERC20("Mock USDT", "USDT") {}

    // Função para imprimir dinheiro falso para qualquer carteira
    function mint(address to, uint256 amount) public {
        _mint(to, amount);
    }
}
