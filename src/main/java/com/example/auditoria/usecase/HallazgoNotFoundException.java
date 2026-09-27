package com.example.auditoria.usecase;

import com.example.auditoria.domain.valueobject.HallazgoId;

public class HallazgoNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public HallazgoNotFoundException(HallazgoId id) {
        super("No existe el hallazgo " + id);
    }
}
