package com.example.ProyectoSaS.TaskController;

import com.example.ProyectoSaS.Services.FacturaService;
import com.example.ProyectoSaS.Services.TaxService;
import com.example.ProyectoSaS.Services.UsuarioService;
import com.example.ProyectoSaS.models.Factura;
import com.example.ProyectoSaS.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/facturacion")
public class FacturacionController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TaxService taxService;

    @GetMapping
    public String mostrarFacturas(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioOpt.get();
        List<Factura> facturas = facturaService.obtenerFacturasPorUsuario(usuarioId);

        model.addAttribute("usuario", usuario);
        model.addAttribute("facturas", facturas);
        model.addAttribute("nombrePais", taxService.obtenerNombrePais(usuario.getPais()));
        model.addAttribute("totalFacturas", facturas.size());
        
        BigDecimal totalMonto = facturas.stream()
            .map(Factura::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalImpuestos = facturas.stream()
            .map(Factura::getMontoImpuesto)
            .filter(m -> m != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalConImpuestos = facturas.stream()
            .map(Factura::getTotalConImpuesto)
            .filter(m -> m != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalMonto", totalMonto);
        model.addAttribute("totalImpuestos", totalImpuestos);
        model.addAttribute("totalConImpuestos", totalConImpuestos);

        return "facturacion";
    }

    @PostMapping("/filtrar-fechas")
    public String filtrarPorFechas(
            @RequestParam(value = "fechaInicio", required = true) String fechaInicio,
            @RequestParam(value = "fechaFin", required = true) String fechaFin,
            HttpSession session, Model model) {
        
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        try {
            LocalDate fechaInicioParsed = LocalDate.parse(fechaInicio.substring(0, 10));
            LocalDate fechaFinParsed = LocalDate.parse(fechaFin.substring(0, 10));
            
            LocalDateTime inicio = LocalDateTime.of(fechaInicioParsed, LocalTime.of(0, 0, 0));
            LocalDateTime fin = LocalDateTime.of(fechaFinParsed, LocalTime.of(23, 59, 59));
            
            List<Factura> facturas = facturaService.obtenerFacturasPorUsuarioYFecha(usuarioId, inicio, fin);
            
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return "redirect:/login";
            }
            Usuario usuario = usuarioOpt.get();

            model.addAttribute("usuario", usuario);
            model.addAttribute("facturas", facturas);
            model.addAttribute("totalFacturas", facturas.size());
            model.addAttribute("filtroAplicado", "Facturas de " + fechaInicio + " a " + fechaFin);
            model.addAttribute("nombrePais", taxService.obtenerNombrePais(usuario.getPais()));
            
            BigDecimal totalMonto = facturas.stream()
                .map(Factura::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalImpuestos = facturas.stream()
                .map(Factura::getMontoImpuesto)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalConImpuestos = facturas.stream()
                .map(Factura::getTotalConImpuesto)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            model.addAttribute("totalMonto", totalMonto);
            model.addAttribute("totalImpuestos", totalImpuestos);
            model.addAttribute("totalConImpuestos", totalConImpuestos);

        } catch (Exception e) {
            model.addAttribute("facturas", new java.util.ArrayList<>());
            model.addAttribute("totalFacturas", 0);
            model.addAttribute("totalMonto", BigDecimal.ZERO);
            model.addAttribute("totalImpuestos", BigDecimal.ZERO);
            model.addAttribute("totalConImpuestos", BigDecimal.ZERO);
            model.addAttribute("error", "Error al filtrar facturas por fechas: " + e.getMessage());
        }

        return "facturacion";
    }

    @PostMapping("/filtrar-montos")
    public String filtrarPorMontos(
            @RequestParam(value = "montoMin", required = false) BigDecimal montoMin,
            @RequestParam(value = "montoMax", required = false) BigDecimal montoMax,
            HttpSession session, Model model) {
        
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        try {
            if (montoMin == null) montoMin = BigDecimal.ZERO;
            if (montoMax == null) montoMax = new BigDecimal("999999");

            List<Factura> facturas = facturaService.obtenerFacturasPorUsuarioYMonto(usuarioId, montoMin, montoMax);
            
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return "redirect:/login";
            }
            Usuario usuario = usuarioOpt.get();

            model.addAttribute("usuario", usuario);
            model.addAttribute("facturas", facturas);
            model.addAttribute("totalFacturas", facturas.size());
            model.addAttribute("filtroAplicado", "Facturas entre €" + montoMin + " y €" + montoMax);
            model.addAttribute("nombrePais", taxService.obtenerNombrePais(usuario.getPais()));
            
            BigDecimal totalMonto = facturas.stream()
                .map(Factura::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalImpuestos = facturas.stream()
                .map(Factura::getMontoImpuesto)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalConImpuestos = facturas.stream()
                .map(Factura::getTotalConImpuesto)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            model.addAttribute("totalMonto", totalMonto);
            model.addAttribute("totalImpuestos", totalImpuestos);
            model.addAttribute("totalConImpuestos", totalConImpuestos);
        } catch (Exception e) {
            model.addAttribute("facturas", new java.util.ArrayList<>());
            model.addAttribute("totalFacturas", 0);
            model.addAttribute("totalMonto", BigDecimal.ZERO);
            model.addAttribute("totalImpuestos", BigDecimal.ZERO);
            model.addAttribute("totalConImpuestos", BigDecimal.ZERO);
            model.addAttribute("error", "Error al filtrar facturas por montos: " + e.getMessage());
        }

        return "facturacion";
    }

    @PostMapping("/filtrar-estado")
    public String filtrarPorEstado(
            @RequestParam(value = "estado", required = true) String estado,
            HttpSession session, Model model) {
        
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        try {
            List<Factura> facturas = facturaService.obtenerFacturasPorUsuarioYEstado(usuarioId, estado);
            
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return "redirect:/login";
            }
            Usuario usuario = usuarioOpt.get();

            model.addAttribute("usuario", usuario);
            model.addAttribute("facturas", facturas);
            model.addAttribute("totalFacturas", facturas.size());
            model.addAttribute("filtroAplicado", "Facturas " + estado);
            model.addAttribute("nombrePais", taxService.obtenerNombrePais(usuario.getPais()));
            
            BigDecimal totalMonto = facturas.stream()
                .map(Factura::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalImpuestos = facturas.stream()
                .map(Factura::getMontoImpuesto)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalConImpuestos = facturas.stream()
                .map(Factura::getTotalConImpuesto)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            model.addAttribute("totalMonto", totalMonto);
            model.addAttribute("totalImpuestos", totalImpuestos);
            model.addAttribute("totalConImpuestos", totalConImpuestos);
        } catch (Exception e) {
            model.addAttribute("facturas", new java.util.ArrayList<>());
            model.addAttribute("totalFacturas", 0);
            model.addAttribute("totalMonto", BigDecimal.ZERO);
            model.addAttribute("totalImpuestos", BigDecimal.ZERO);
            model.addAttribute("totalConImpuestos", BigDecimal.ZERO);
            model.addAttribute("error", "Error al filtrar facturas por estado: " + e.getMessage());
        }

        return "facturacion";
    }

    @PostMapping("/marcar-pagada")
    public String marcarComoPagada(
            @RequestParam(value = "facturaId", required = true) Long facturaId,
            HttpSession session) {
        
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        facturaService.marcarComoPagada(facturaId);
        return "redirect:/facturacion";
    }

    @GetMapping("/{facturaId}")
    public String verDetalleFactura(
            @PathVariable Long facturaId,
            HttpSession session, Model model) {
        
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Optional<Factura> facturaOpt = facturaService.obtenerFacturasPorUsuario(usuarioId)
            .stream()
            .filter(f -> f.getId().equals(facturaId))
            .findFirst();

        if (facturaOpt.isEmpty()) {
            model.addAttribute("error", "Factura no encontrada");
            return "redirect:/facturacion";
        }

        Factura factura = facturaOpt.get();
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);
        Usuario usuario = usuarioOpt.get();

        model.addAttribute("factura", factura);
        model.addAttribute("usuario", usuario);
        model.addAttribute("nombrePais", taxService.obtenerNombrePais(usuario.getPais()));

        return "detalle-factura";
    }

    @PostMapping("/crear")
    public String crearFactura(
            @RequestParam String numeroFactura,
            @RequestParam BigDecimal monto,
            @RequestParam(defaultValue = "21") BigDecimal porcentajeImpuesto,
            @RequestParam(defaultValue = "PENDIENTE") String estado,
            HttpSession session,
            Model model) {
        
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        try {
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return "redirect:/login";
            }

            Usuario usuario = usuarioOpt.get();
            
            Factura factura = new Factura();
            factura.setUsuario(usuario);
            factura.setNumeroFactura(numeroFactura);
            factura.setFechaEmision(LocalDateTime.now());
            factura.setFechaVencimiento(LocalDateTime.now().plusDays(30));
            factura.setMonto(monto);
            factura.setPorcentajeImpuesto(porcentajeImpuesto);
            
            BigDecimal montoImpuesto = monto.multiply(porcentajeImpuesto).divide(new BigDecimal("100"));
            BigDecimal totalConImpuesto = monto.add(montoImpuesto);
            
            factura.setMontoImpuesto(montoImpuesto);
            factura.setTotalConImpuesto(totalConImpuesto);
            factura.setEstado(estado);
            
            facturaService.crearFactura(factura);
            
            model.addAttribute("mensaje", "Factura creada exitosamente");
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear factura: " + e.getMessage());
        }

        return "redirect:/facturacion";
    }
}
