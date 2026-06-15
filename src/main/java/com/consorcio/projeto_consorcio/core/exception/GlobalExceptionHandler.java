package com.consorcio.projeto_consorcio.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice//isso define ao spring que essa classe vai monitorar toda a api
public class GlobalExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ExceptionResponseDTO> registroNaoEncontrado(EntidadeNaoEncontradaException exception, HttpServletRequest request){
        ExceptionResponseDTO erro = new ExceptionResponseDTO(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ExceptionResponseDTO> regraDeNegocioViolada(RegraDeNegocioException exception, HttpServletRequest request){
        ExceptionResponseDTO erro = new ExceptionResponseDTO(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Regra de negócio violada",
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ExceptionResponseDTO> acessoNegado(AcessoNegadoException exception, HttpServletRequest request){
        ExceptionResponseDTO erro = new ExceptionResponseDTO(
                Instant.now(),
                HttpStatus.FORBIDDEN.value(),
                "Acesso não permitido",
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> erroValidacao(MethodArgumentNotValidException exception, HttpServletRequest request){
        Map<String, String> erros = new HashMap<>(); //pra guarda os erros retornados dos DTOs
        //o spring guarda dentro desse BindingReuslt
        for (FieldError erro : exception.getBindingResult().getFieldErrors()){
            erros.put(erro.getField(), erro.getDefaultMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp",Instant.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("erro", "Erro de Validação");
        response.put("caminho", request.getRequestURI());
        response.put("campos", erros);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponseDTO> erroDoDancoDeDados(DataIntegrityViolationException exception, HttpServletRequest request){
        ExceptionResponseDTO erro = new ExceptionResponseDTO(
                Instant.now(),
                HttpStatus.CONFLICT.value(), //409
                "Conflito de dados",
                "Operação não permitida. O registro já existe ou está vinculado a outros registros!",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDTO> exceptionsEmGeral(Exception exception, HttpServletRequest request){
        ExceptionResponseDTO erro = new ExceptionResponseDTO(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno no servidor",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }


    //BlockchainUnavailableException mais pra frente

}
