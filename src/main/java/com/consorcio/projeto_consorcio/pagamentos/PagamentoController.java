package com.consorcio.projeto_consorcio.pagamentos;

import com.consorcio.projeto_consorcio.pagamentos.dto.PagamentoResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {
    private final PagamentoRepository pagamentoRepository;
    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoRepository pagamentoRepository, PagamentoService pagamentoService){
        this.pagamentoRepository = pagamentoRepository;
        this.pagamentoService = pagamentoService;
    }

    @GetMapping("/cota/{cotaId}")
    public ResponseEntity<List<PagamentoResponseDTO>> extrato(@PathVariable Long cotaId){
        List<PagamentoResponseDTO> response = pagamentoService.buscarExtrato(cotaId);
        return ResponseEntity.ok(response);
    }


    /*@PatchMapping("/{pagamentoId}/webhook")
    public ResponseEntity<String> confirmaPagamento(@RequestBody @Valid PagamentoRequestDTO request, @PathVariable String pagamentoId){
        String response = pagamentoService.registrarPagamentoWeb3(pagamentoId, )
    }*/

}
