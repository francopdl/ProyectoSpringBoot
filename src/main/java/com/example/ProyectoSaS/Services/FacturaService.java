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

    public Factura generarFactura(Usuario usuario, Suscripcion suscripcion, BigDecimal monto) {
        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setSuscripcion(suscripcion);
        factura.setMonto(monto);
        
        // Generar número de factura único
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

    public void marcarComoPagada(Long facturaId) {
        facturaRepository.findById(facturaId).ifPresent(factura -> {
            factura.setEstado("PAGADA");
            facturaRepository.save(factura);
        });
    }
}
