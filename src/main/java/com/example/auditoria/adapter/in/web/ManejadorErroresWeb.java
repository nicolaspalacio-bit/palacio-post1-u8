package com.example.auditoria.adapter.in.web;

import com.example.auditoria.domain.valueobject.TransicionInvalidaException;
import com.example.auditoria.usecase.HallazgoNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/** Traduce las excepciones del dominio y de los casos de uso a codigos HTTP. */
@RestControllerAdvice
public class ManejadorErroresWeb {

    public record ErrorRespuesta(int estado, String error, String mensaje) {}

    @ExceptionHandler({TransicionInvalidaException.class, IllegalStateException.class,
                       IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRespuesta solicitudInvalida(RuntimeException ex) {
        return new ErrorRespuesta(400, ex.getClass().getSimpleName(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRespuesta cuerpoInvalido(MethodArgumentNotValidException ex) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining("; "));
        return new ErrorRespuesta(400, "CuerpoInvalido", detalle);
    }

    @ExceptionHandler(HallazgoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorRespuesta noEncontrado(HallazgoNotFoundException ex) {
        return new ErrorRespuesta(404, "HallazgoNotFoundException", ex.getMessage());
    }
}
