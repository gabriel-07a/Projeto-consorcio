package com.consorcio.projeto_consorcio.pagamentos;

import com.consorcio.projeto_consorcio.pagamentos.dto.PagamentoResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {
    @Autowired
    private PagamentoRepository pagamentoRepository;
    
    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping("/cota/{cotaId}")
    public ResponseEntity<List<PagamentoResponseDTO>> extrato(@PathVariable Long cotaId){
        List<PagamentoResponseDTO> response = pagamentoService.buscarExtrato(cotaId);
        return ResponseEntity.ok(response);
    }
}
