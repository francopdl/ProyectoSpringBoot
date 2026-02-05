package com.example.ProyectoSaS.repositories;

import com.example.ProyectoSaS.models.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    List<Factura> findByUsuarioId(Long usuarioId);
    Optional<Factura> findByNumeroFactura(String numeroFactura);
}
