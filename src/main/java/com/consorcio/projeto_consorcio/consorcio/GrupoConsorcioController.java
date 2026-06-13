package com.consorcio.projeto_consorcio.consorcio;

import com.consorcio.projeto_consorcio.consorcio.dto.ApagarGrupoConsorcioResponseDTO;
import com.consorcio.projeto_consorcio.consorcio.dto.CriarGrupoConsorcioRequestDTO;
import com.consorcio.projeto_consorcio.consorcio.dto.CriarGrupoConsorcioResponseDTO;
import com.consorcio.projeto_consorcio.consorcio.dto.GrupoConsorcioResponseDTO;
import com.consorcio.projeto_consorcio.consorcio.enums.StatusGrupo;
import com.consorcio.projeto_consorcio.cota.dto.CotaResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grupos")
public class GrupoConsorcioController {
    private final GrupoConsorcioService grupoConsorcioService;
    private final GrupoConsorcioRepository grupoConsorcioRepository;

    public GrupoConsorcioController(GrupoConsorcioService grupoConsorcioService, GrupoConsorcioRepository grupoConsorcioRepository){
        this.grupoConsorcioRepository = grupoConsorcioRepository;
        this.grupoConsorcioService = grupoConsorcioService;
    }

    @GetMapping
    public ResponseEntity<List<GrupoConsorcioResponseDTO>> buscarGrupos(@RequestParam(required = false)StatusGrupo status){
        List<GrupoConsorcioResponseDTO> response = grupoConsorcioService.buscarGrupos(status);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/{grupoId}")
    public ResponseEntity<GrupoConsorcioResponseDTO> buscarGrupo(@PathVariable Long grupoId){
        GrupoConsorcioResponseDTO response = grupoConsorcioService.buscarGrupo(grupoId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{grupoId}/cotas")
    public ResponseEntity<List<CotaResponseDTO>> listarCotasDoGrupo(@PathVariable Long grupoId){
        List<CotaResponseDTO> response = grupoConsorcioService.listarCotas(grupoId);

        return ResponseEntity.ok(response);

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

    @PatchMapping("/encerrar/{grupoId}")
    public ResponseEntity<String> encerrarGrupoConsorcio(@PathVariable Long grupoId){
        String stringRetorno = grupoConsorcioService.encerrarGrupo(grupoId);
        return ResponseEntity.ok(stringRetorno);
    }

    @DeleteMapping("/apagar/{grupoId}")
    public ResponseEntity<ApagarGrupoConsorcioResponseDTO> apagarGrupoConsorcio(@PathVariable Long grupoId, @RequestParam String enderecoContrato){
        ApagarGrupoConsorcioResponseDTO response = grupoConsorcioService.apagarGrupoConsorcio(grupoId, enderecoContrato);

        return ResponseEntity.ok(response);
    }



}
