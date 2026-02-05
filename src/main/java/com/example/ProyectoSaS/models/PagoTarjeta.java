package com.example.ProyectoSaS.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos_tarjeta")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PagoTarjeta extends Pago implements Serializable {
    @Column(nullable = false)
    private String numeroTarjeta;

    @Column(nullable = false)
    private String titular;

    @Column(nullable = false)
    private String fechaVencimiento;
}
