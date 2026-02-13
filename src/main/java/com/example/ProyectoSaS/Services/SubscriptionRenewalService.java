package com.example.ProyectoSaS.Services;

import com.example.ProyectoSaS.enums.EstadoSuscripcion;
import com.example.ProyectoSaS.enums.TipoPlan;
import com.example.ProyectoSaS.models.Factura;
import com.example.ProyectoSaS.models.Suscripcion;
import com.example.ProyectoSaS.models.Usuario;
import com.example.ProyectoSaS.repositories.SuscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionRenewalService {

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private TaxService taxService;

    @Scheduled(cron = "0 2 * * * *")
    public void renovarSuscripcionesVencidas() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Suscripcion> suscripciones = suscripcionRepository.findAll();
        
        for (Suscripcion suscripcion : suscripciones) {
            if (suscripcion.getEstado() == EstadoSuscripcion.ACTIVA && 
                suscripcion.getFechaProximoRenovacion().isBefore(ahora)) {
                renovarSuscripcion(suscripcion);
            }
        }
    }

    public void renovarSuscripcion(Suscripcion suscripcion) {
        try {
            Usuario usuario = suscripcion.getUsuario();
            BigDecimal monto = obtenerMontoPorPlan(suscripcion.getTipoPlan());
            
            BigDecimal impuesto = taxService.calcularMontoImpuesto(monto, usuario.getPais());
            BigDecimal total = monto.add(impuesto);
            
            Factura factura = new Factura();
            factura.setUsuario(usuario);
            factura.setSuscripcion(suscripcion);
            factura.setMonto(monto);
            factura.setPorcentajeImpuesto(taxService.obtenerTasaImpuesto(usuario.getPais()));
            factura.setMontoImpuesto(impuesto);
            factura.setTotalConImpuesto(total);
            factura.setNumeroFactura("REN-" + System.currentTimeMillis() + "-" + usuario.getId());
            factura.setEstado("PENDIENTE");
            
            facturaService.generarFactura(usuario, suscripcion, total);
            
            suscripcion.setFechaProximoRenovacion(LocalDateTime.now().plusMonths(1));
            suscripcionRepository.save(suscripcion);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private BigDecimal obtenerMontoPorPlan(TipoPlan plan) {
        return switch (plan) {
            case BASIC -> new BigDecimal("9.99");
            case PREMIUM -> new BigDecimal("29.99");
            case ENTERPRISE -> new BigDecimal("99.99");
        };
    }

    public void cancelarSuscripcion(Long usuarioId) {
        Optional<Suscripcion> suscripcion = suscripcionRepository.findByUsuarioId(usuarioId);
        if (suscripcion.isPresent()) {
            suscripcion.get().setEstado(EstadoSuscripcion.CANCELADA);
            suscripcion.get().setFechaCancelacion(LocalDateTime.now());
            suscripcionRepository.save(suscripcion.get());
        }
    }
}
