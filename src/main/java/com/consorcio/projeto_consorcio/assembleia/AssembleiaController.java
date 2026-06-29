package com.consorcio.projeto_consorcio.assembleia;

import com.consorcio.projeto_consorcio.assembleia.dto.AssembleiaResponseDTO;
import com.consorcio.projeto_consorcio.consorcio.enums.TipoLance;
import com.consorcio.projeto_consorcio.cota.Cota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assembleia")
public class AssembleiaController {
    @Autowired
    private AssembleiaService assembleiaService;

    @PostMapping("/grupos/{grupoId}/sortear")
    public ResponseEntity<AssembleiaResponseDTO> sortearManual(@PathVariable Long grupoId){
        Cota vencedor = assembleiaService.realizarSorteio(grupoId);

        AssembleiaResponseDTO dto = new AssembleiaResponseDTO(
                "Sorteio realizado com suscesso!",
                vencedor.getNumeroCota(),
                vencedor.getUsuario().getNome()
        );

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/grupos/{grupoId}/contemplar-lance")
    public ResponseEntity<AssembleiaResponseDTO> contemplarPorLanceManual(
            @PathVariable Long grupoId,
            @RequestParam TipoLance tipoLance
    ) {
        Cota vencedor = assembleiaService.realizarAssembleiaPorLance(grupoId, tipoLance);

        AssembleiaResponseDTO dto = new AssembleiaResponseDTO(
                "Assembleia de lance realizada com sucesso!",
                vencedor.getNumeroCota(),
                vencedor.getUsuario().getNome()
        );

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/grupos/{grupoId}/avancar-ciclo")
    public ResponseEntity<String> avancarCicloManual(@PathVariable Long grupoId) {
        assembleiaService.avancarCicloManualmente(grupoId);
        return ResponseEntity.ok("Ciclo do grupo avançado com sucesso na blockchain!");
    }
}
