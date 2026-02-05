package com.example.ProyectoSaS.models;

import com.example.ProyectoSaS.enums.TipoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public class Pago implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPago tipoPago;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaPago;

    @PrePersist
    protected void onCreate() {
        fechaPago = LocalDateTime.now();
        if (estado == null) {
            estado = "COMPLETADO";
        }
    }
}
