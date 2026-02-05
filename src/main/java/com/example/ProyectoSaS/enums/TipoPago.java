package com.example.ProyectoSaS.enums;

public enum TipoPago {
    TARJETA_CREDITO("Tarjeta de Crédito"),
    PAYPAL("PayPal"),
    TRANSFERENCIA("Transferencia Bancaria");

    private final String descripcion;

    TipoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
