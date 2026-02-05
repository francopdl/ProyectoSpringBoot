package com.example.ProyectoSaS.enums;

public enum TipoPlan {
    BASIC(9.99, "Plan Básico"),
    PREMIUM(29.99, "Plan Premium"),
    ENTERPRISE(99.99, "Plan Enterprise");

    private final double precio;
    private final String descripcion;

    TipoPlan(double precio, String descripcion) {
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
