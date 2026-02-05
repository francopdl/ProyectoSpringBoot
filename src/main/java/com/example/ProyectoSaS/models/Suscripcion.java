package com.example.ProyectoSaS.models;

import com.example.ProyectoSaS.enums.EstadoSuscripcion;
import com.example.ProyectoSaS.enums.TipoPlan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "suscripciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class Suscripcion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPlan tipoPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSuscripcion estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaProximoRenovacion;

    @Column(nullable = true)
    private LocalDateTime fechaCancelacion;

    @PrePersist
    protected void onCreate() {
        fechaInicio = LocalDateTime.now();
        if (fechaProximoRenovacion == null) {
            fechaProximoRenovacion = fechaInicio.plusMonths(1);
        }
        if (estado == null) {
            estado = EstadoSuscripcion.ACTIVA;
        }
    }
}
