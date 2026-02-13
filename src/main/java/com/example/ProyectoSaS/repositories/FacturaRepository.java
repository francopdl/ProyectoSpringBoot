package com.example.ProyectoSaS.repositories;

import com.example.ProyectoSaS.models.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    List<Factura> findByUsuarioId(Long usuarioId);
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    
    /**
     * Filtrar facturas por rango de fechas
     */
    @Query("SELECT f FROM Factura f WHERE f.usuario.id = :usuarioId AND f.fechaEmision >= :fechaInicio AND f.fechaEmision <= :fechaFin ORDER BY f.fechaEmision DESC")
    List<Factura> findByUsuarioIdAndFechaRange(
        @Param("usuarioId") Long usuarioId,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    /**
     * Filtrar facturas por monto mínimo y máximo
     */
    @Query("SELECT f FROM Factura f WHERE f.usuario.id = :usuarioId AND f.monto >= :montoMin AND f.monto <= :montoMax ORDER BY f.monto DESC")
    List<Factura> findByUsuarioIdAndMontoRange(
        @Param("usuarioId") Long usuarioId,
        @Param("montoMin") BigDecimal montoMin,
        @Param("montoMax") BigDecimal montoMax
    );
    
    /**
     * Filtrar facturas por estado
     */
    List<Factura> findByUsuarioIdAndEstado(Long usuarioId, String estado);
    
    /**
     * Filtrar facturas por usuario, estado y rango de fechas
     */
    @Query("SELECT f FROM Factura f WHERE f.usuario.id = :usuarioId AND f.estado = :estado AND f.fechaEmision >= :fechaInicio AND f.fechaEmision <= :fechaFin ORDER BY f.fechaEmision DESC")
    List<Factura> findByUsuarioIdEstadoAndFechaRange(
        @Param("usuarioId") Long usuarioId,
        @Param("estado") String estado,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    /**
     * Obtener todas las facturas con ordenamiento
     */
    @Query("SELECT f FROM Factura f ORDER BY f.fechaEmision DESC")
    List<Factura> findAllFacturasOrdenadas();
    
    /**
     * Obtener facturas por estado general (sin filtro de usuario)
     */
    List<Factura> findByEstado(String estado);
}
