package com.consorcio.projeto_consorcio.assembleia.strategy;

import com.consorcio.projeto_consorcio.core.exception.RegraDeNegocioException;
import com.consorcio.projeto_consorcio.cota.Cota;

import java.util.List;
import java.util.Random;

public class SorteioStrategy implements ContemplacaoStrategy{

    @Override
    public Cota elegerVencedor(List<Cota> cotasElegiveis) {
        if(cotasElegiveis == null || cotasElegiveis.isEmpty()){
            throw new RegraDeNegocioException("Erro: Não há participantes elegiveis para o sorteio.");
        }
        //classe random que retorna um número aleatorio
        Random random = new Random();
        //rando.nextInt retorna um num aleatorio entre 0 e o parametro passado
        int indexDoSorteado = random.nextInt(cotasElegiveis.size());
        //retorna o vencedor
        return cotasElegiveis.get(indexDoSorteado);
    }
}
