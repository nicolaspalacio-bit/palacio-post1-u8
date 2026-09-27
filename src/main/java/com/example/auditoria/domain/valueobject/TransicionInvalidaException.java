package com.example.auditoria.domain.valueobject;

public class TransicionInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TransicionInvalidaException(EstadoHallazgo actual, EstadoHallazgo destino) {
        super("No se puede transicionar de " + actual + " a " + destino);
    }
}
