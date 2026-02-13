package com.example.ProyectoSaS.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class Factura implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "suscripcion_id", nullable = false)
    private Suscripcion suscripcion;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = true)
    private BigDecimal porcentajeImpuesto;

    @Column(nullable = true)
    private BigDecimal montoImpuesto;

    @Column(nullable = true)
    private BigDecimal totalConImpuesto;

    @Column(nullable = false, unique = true)
    private String numeroFactura;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false)
    private LocalDateTime fechaVencimiento;

    @PrePersist
    protected void onCreate() {
        fechaEmision = LocalDateTime.now();
        if (fechaVencimiento == null) {
            fechaVencimiento = fechaEmision.plusDays(30);
        }
        if (estado == null) {
            estado = "PENDIENTE";
        }
    }
}
