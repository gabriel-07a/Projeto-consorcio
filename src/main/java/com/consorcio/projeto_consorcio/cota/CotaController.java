package com.consorcio.projeto_consorcio.cota;

import com.consorcio.projeto_consorcio.cota.dto.CancelarCotaRequestDTO;
import com.consorcio.projeto_consorcio.cota.dto.ComprarCotaRequestDTO;
import com.consorcio.projeto_consorcio.cota.dto.CotaResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cotas")
public class CotaController {
    private final CotaRepository cotaRepository;
    private final CotaService cotaService;

    public CotaController(CotaRepository cotaRepository, CotaService cotaService){
        this.cotaRepository = cotaRepository;
        this.cotaService = cotaService;
    }

    //esse responseEntity é uma classe que serve para montar uma pacote http completo para mim
    @PostMapping("/comprar")
    public ResponseEntity<CotaResponseDTO> comprarCota(@RequestBody ComprarCotaRequestDTO requestDTO){
        CotaResponseDTO response =  cotaService.comprarCota(requestDTO.usuarioId(), requestDTO.grupoId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CotaResponseDTO> cancelarCota(@PathVariable Long id){
        CotaResponseDTO response = cotaService.cancelarCota(id);

        return ResponseEntity.ok(response);
    }

}
