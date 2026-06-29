package com.consorcio.projeto_consorcio.cota;

import com.consorcio.projeto_consorcio.cota.dto.CancelarCotaRequestDTO;
import com.consorcio.projeto_consorcio.cota.dto.ComprarCotaRequestDTO;
import com.consorcio.projeto_consorcio.cota.dto.CotaResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cotas")
public class CotaController {
    @Autowired
    private CotaRepository cotaRepository;
    
    @Autowired
    private CotaService cotaService;

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
