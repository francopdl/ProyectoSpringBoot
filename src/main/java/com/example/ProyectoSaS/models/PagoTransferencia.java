package com.example.ProyectoSaS.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "pagos_transferencia")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PagoTransferencia extends Pago implements Serializable {
    @Column(nullable = false)
    private String numeroConversacion;

    @Column(nullable = false)
    private String banco;

    @Column(nullable = false)
    private String titularCuenta;
}
