package com.consorcio.projeto_consorcio.assembleia;

import com.consorcio.projeto_consorcio.assembleia.dto.AssembleiaResponseDTO;
import com.consorcio.projeto_consorcio.cota.Cota;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assembleia")
public class AssembleiaController {
    private final AssembleiaService assembleiaService;

    public AssembleiaController(AssembleiaService assembleiaService){
        this.assembleiaService = assembleiaService;
    }

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


}
