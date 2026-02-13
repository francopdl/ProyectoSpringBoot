package com.example.ProyectoSaS.Services;

import com.example.ProyectoSaS.models.Factura;
import com.example.ProyectoSaS.models.Suscripcion;
import com.example.ProyectoSaS.models.Usuario;
import com.example.ProyectoSaS.repositories.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FacturaService {
    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private TaxService taxService;

    public Factura generarFactura(Usuario usuario, Suscripcion suscripcion, BigDecimal monto) {
        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setSuscripcion(suscripcion);
        factura.setMonto(monto);
        
        BigDecimal porcentajeImpuesto = taxService.obtenerTasaImpuesto(usuario.getPais());
        BigDecimal montoImpuesto = taxService.calcularMontoImpuesto(monto, usuario.getPais());
        BigDecimal totalConImpuesto = monto.add(montoImpuesto);
        
        factura.setPorcentajeImpuesto(porcentajeImpuesto);
        factura.setMontoImpuesto(montoImpuesto);
        factura.setTotalConImpuesto(totalConImpuesto);
        
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String numeroFactura = "FAC-" + ahora.format(formatter) + "-" + usuario.getId();
        factura.setNumeroFactura(numeroFactura);
        
        factura.setEstado("PENDIENTE");
        return facturaRepository.save(factura);
    }

    public List<Factura> obtenerFacturasPorUsuario(Long usuarioId) {
        return facturaRepository.findByUsuarioId(usuarioId);
    }

    public List<Factura> obtenerFacturasPorUsuarioYFecha(Long usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return facturaRepository.findByUsuarioIdAndFechaRange(usuarioId, fechaInicio, fechaFin);
    }

    public List<Factura> obtenerFacturasPorUsuarioYMonto(Long usuarioId, BigDecimal montoMin, BigDecimal montoMax) {
        return facturaRepository.findByUsuarioIdAndMontoRange(usuarioId, montoMin, montoMax);
    }

    public List<Factura> obtenerFacturasPorUsuarioYEstado(Long usuarioId, String estado) {
        return facturaRepository.findByUsuarioIdAndEstado(usuarioId, estado);
    }

    public List<Factura> obtenerFacturasFiltradas(Long usuarioId, String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return facturaRepository.findByUsuarioIdEstadoAndFechaRange(usuarioId, estado, fechaInicio, fechaFin);
    }

    public void marcarComoPagada(Long facturaId) {
        facturaRepository.findById(facturaId).ifPresent(factura -> {
            factura.setEstado("PAGADA");
            facturaRepository.save(factura);
        });
    }

    public List<Factura> obtenerTodasLasFacturas() {
        return facturaRepository.findAllFacturasOrdenadas();
    }

    public Factura crearFactura(Factura factura) {
        if (factura.getFechaEmision() == null) {
            factura.setFechaEmision(LocalDateTime.now());
        }
        if (factura.getFechaVencimiento() == null) {
            factura.setFechaVencimiento(factura.getFechaEmision().plusDays(30));
        }
        return facturaRepository.save(factura);
    }
}
