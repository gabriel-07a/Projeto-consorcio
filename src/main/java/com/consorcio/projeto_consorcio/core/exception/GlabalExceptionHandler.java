package com.consorcio.projeto_consorcio.core.exception;

import com.consorcio.projeto_consorcio.core.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice//isso define ao spring que essa classe vai vigiar o codigo inteiro
public class GlabalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> tratarRetornoDeErrosPorRegraDeNegocio(RuntimeException exception){
        ErrorResponseDTO error = new ErrorResponseDTO(
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


}
