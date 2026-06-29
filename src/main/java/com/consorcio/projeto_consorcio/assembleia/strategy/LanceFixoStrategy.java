package com.consorcio.projeto_consorcio.assembleia.strategy;

import com.consorcio.projeto_consorcio.core.exception.RegraDeNegocioException;
import com.consorcio.projeto_consorcio.cota.Cota;
import com.consorcio.projeto_consorcio.lances.Lance;

import java.util.List;
import java.util.Random;

public class LanceFixoStrategy implements ContemplacaoStrategy {
    private final List<Lance> lancesDoCiclo;

    public LanceFixoStrategy(List<Lance> lancesDoCiclo) {
        this.lancesDoCiclo = lancesDoCiclo;
    }

    @Override
    public Cota elegerVencedor(List<Cota> cotasElegiveis) {
        if (lancesDoCiclo == null || lancesDoCiclo.isEmpty()) {
            throw new RegraDeNegocioException("Erro: Não há lances registrados para este ciclo.");
        }

        //filtra os lances fixos elegíveis
        List<Lance> lancesValidos = lancesDoCiclo.stream()
                .filter(l -> l.getTipoLance().equalsIgnoreCase("FIXO"))
                .filter(l -> cotasElegiveis.contains(l.getCota()))
                .toList();

        if (lancesValidos.isEmpty()) {
            throw new RegraDeNegocioException("Erro: Nenhum lance fixo elegível encontrado para este ciclo.");
        }

        //sorteia entre os lances fixos
        Random random = new Random();
        int indexVencedor = random.nextInt(lancesValidos.size());
        Lance lanceVencedor = lancesValidos.get(indexVencedor);

        lanceVencedor.setVencedor(true);
        return lanceVencedor.getCota();
    }
}
