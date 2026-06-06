package com.consorcio.projeto_consorcio.assembleia.strategy;

import com.consorcio.projeto_consorcio.cota.Cota;

import java.util.List;

public interface ContemplacaoStrategy {
    //elege o vencedor recebendo uma lista de participantes elegiveis
    Cota elegerVencedor(List<Cota> cotasElegiveis);
}
