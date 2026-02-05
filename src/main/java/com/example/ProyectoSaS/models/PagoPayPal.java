package com.example.ProyectoSaS.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "pagos_paypal")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PagoPayPal extends Pago implements Serializable {
    @Column(nullable = false)
    private String cuentaPayPal;

    @Column(nullable = false)
    private String transactionId;
}
