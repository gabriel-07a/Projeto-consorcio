package com.consorcio.projeto_consorcio.consorcio;

import com.consorcio.projeto_consorcio.consorcio.dto.ApagarGrupoConsorcioResponseDTO;
import com.consorcio.projeto_consorcio.consorcio.dto.CriarGrupoConsorcioRequestDTO;
import com.consorcio.projeto_consorcio.consorcio.dto.CriarGrupoConsorcioResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/grupos")
public class GrupoConsorcioController {
    private final GrupoConsorcioService grupoConsorcioService;
    private final GrupoConsorcioRepository grupoConsorcioRepository;

    public GrupoConsorcioController(GrupoConsorcioService grupoConsorcioService, GrupoConsorcioRepository grupoConsorcioRepository){
        this.grupoConsorcioRepository = grupoConsorcioRepository;
        this.grupoConsorcioService = grupoConsorcioService;
    }

    @PostMapping("/criar")
    public ResponseEntity<CriarGrupoConsorcioResponseDTO> criarGrupoConsorcio(@RequestBody @Valid CriarGrupoConsorcioRequestDTO requestDTO){
        CriarGrupoConsorcioResponseDTO response = grupoConsorcioService.criarNovoGrupoConsorcio(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/iniciar/{grupoId}")
    public ResponseEntity<String> iniciarGrupoConsorcio(@PathVariable Long grupoId){
        String stringRetorno = grupoConsorcioService.iniciarGrupo(grupoId);

        return ResponseEntity.ok(stringRetorno);
    }

    @DeleteMapping("/apagar/{grupoId}")
    public ResponseEntity<ApagarGrupoConsorcioResponseDTO> apagarGrupoConsorcio(@PathVariable Long grupoId, @RequestParam String enderecoContrato){
        ApagarGrupoConsorcioResponseDTO response = grupoConsorcioService.apagarGrupoConsorcio(grupoId, enderecoContrato);

        return ResponseEntity.ok(response);
    }



}
