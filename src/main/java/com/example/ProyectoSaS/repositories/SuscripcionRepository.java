package com.example.ProyectoSaS.repositories;

import com.example.ProyectoSaS.models.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    Optional<Suscripcion> findByUsuarioId(Long usuarioId);
}
