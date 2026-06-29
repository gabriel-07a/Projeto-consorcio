package com.consorcio.projeto_consorcio.assembleia.strategy;

import com.consorcio.projeto_consorcio.core.exception.RegraDeNegocioException;
import com.consorcio.projeto_consorcio.cota.Cota;
import com.consorcio.projeto_consorcio.lances.Lance;

import java.util.Comparator;
import java.util.List;

public class LanceLivreStrategy implements ContemplacaoStrategy {
    private final List<Lance> lancesDoCiclo;

    public LanceLivreStrategy(List<Lance> lancesDoCiclo) {
        this.lancesDoCiclo = lancesDoCiclo;
    }

    @Override
    public Cota elegerVencedor(List<Cota> cotasElegiveis) {
        if (lancesDoCiclo == null || lancesDoCiclo.isEmpty()) {
            throw new RegraDeNegocioException("Erro: Não há lances registrados para este ciclo.");
        }


        List<Lance> lancesValidos = lancesDoCiclo.stream()
                .filter(l -> l.getTipoLance().equalsIgnoreCase("LIVRE"))
                .filter(l -> cotasElegiveis.contains(l.getCota()))
                .toList();

        if (lancesValidos.isEmpty()) {
            throw new RegraDeNegocioException("Erro: Nenhum lance livre elegível encontrado para este ciclo.");
        }

        //pega o maior lance
        Lance lanceVencedor = lancesValidos.stream()
                .max(Comparator.comparing(Lance::getValorLance)
                        .thenComparing((l1, l2) -> l2.getId().compareTo(l1.getId())))
                .orElseThrow(() -> new RegraDeNegocioException("Erro ao calcular o lance livre vencedor."));

        lanceVencedor.setVencedor(true);
        return lanceVencedor.getCota();
    }
}
