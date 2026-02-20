package com.example.ProyectoSaS.TaskController;

import com.example.ProyectoSaS.Services.FacturaService;
import com.example.ProyectoSaS.Services.FacturaPDFService;
import com.example.ProyectoSaS.Services.SuscripcionService;
import com.example.ProyectoSaS.Services.TaxService;
import com.example.ProyectoSaS.Services.UsuarioService;
import com.example.ProyectoSaS.models.Factura;
import com.example.ProyectoSaS.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private SuscripcionService suscripcionService;

    @Autowired
    private FacturaPDFService facturaPDFService;

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
        
        // Get all facturas and filter by usuario in this application
        List<Factura> todasLasFacturas = facturaService.obtenerTodasLasFacturas();
        List<Factura> facturas = todasLasFacturas.stream()
            .filter(f -> f.getUsuario().getId().equals(usuarioId))
            .toList();
        
        // Ensure facturas is never null
        if (facturas == null) {
            facturas = new java.util.ArrayList<>();
        }

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
            
            // Get all facturas and filter by usuario and date range
            List<Factura> todasLasFacturas = facturaService.obtenerTodasLasFacturas();
            List<Factura> facturas = todasLasFacturas.stream()
                .filter(f -> f.getUsuario().getId().equals(usuarioId))
                .filter(f -> f.getFechaEmision().isAfter(inicio) && f.getFechaEmision().isBefore(fin))
                .toList();
            
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
            BigDecimal finalMontoMin = montoMin != null ? montoMin : BigDecimal.ZERO;
            BigDecimal finalMontoMax = montoMax != null ? montoMax : new BigDecimal("999999");

            // Get all facturas and filter by usuario and monto range
            List<Factura> todasLasFacturas = facturaService.obtenerTodasLasFacturas();
            List<Factura> facturas = todasLasFacturas.stream()
                .filter(f -> f.getUsuario().getId().equals(usuarioId))
                .filter(f -> f.getMonto().compareTo(finalMontoMin) >= 0 && f.getMonto().compareTo(finalMontoMax) <= 0)
                .toList();
            
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return "redirect:/login";
            }
            Usuario usuario = usuarioOpt.get();

            model.addAttribute("usuario", usuario);
            model.addAttribute("facturas", facturas);
            model.addAttribute("totalFacturas", facturas.size());
            model.addAttribute("filtroAplicado", "Facturas entre €" + finalMontoMin + " y €" + finalMontoMax);
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
            // Get all facturas and filter by usuario and estado
            List<Factura> todasLasFacturas = facturaService.obtenerTodasLasFacturas();
            List<Factura> facturas = todasLasFacturas.stream()
                .filter(f -> f.getUsuario().getId().equals(usuarioId))
                .filter(f -> f.getEstado().equals(estado))
                .toList();
            
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

        Optional<Factura> facturaOpt = facturaService.obtenerTodasLasFacturas().stream()
            .filter(f -> f.getId().equals(facturaId) && f.getUsuario().getId().equals(usuarioId))
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
            
            // Get user subscription - REQUIRED
            Optional<com.example.ProyectoSaS.models.Suscripcion> suscripcionOpt = suscripcionService.obtenerSuscripcionPorUsuario(usuarioId);
            if (suscripcionOpt.isEmpty()) {
                model.addAttribute("error", "El usuario no tiene una suscripción activa");
                return mostrarFacturas(session, model);
            }
            
            Factura factura = new Factura();
            factura.setUsuario(usuario);
            factura.setSuscripcion(suscripcionOpt.get());
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
            return mostrarFacturas(session, model);
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear factura: " + e.getMessage());
            return mostrarFacturas(session, model);
        }
    }

    @GetMapping("/descargar")
    public ResponseEntity<byte[]> descargarFacturas(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            List<Factura> facturas = facturaService.obtenerFacturasPorUsuario(usuarioId);
            byte[] pdfBytes = facturaPDFService.generarPDFMultiplesFacturas(facturas);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"facturas.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/configuracion")
    public String mostrarConfiguracion(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioOpt.get();
        model.addAttribute("usuario", usuario);
        model.addAttribute("pais", usuario.getPais());

        return "configuracion-facturacion";
    }

    @PostMapping("/configuracion/actualizar")
    public String actualizarConfiguracion(
            @RequestParam(required = false) String pais,
            @RequestParam(required = false) String empresa,
            HttpSession session,
            Model model) {
        
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        try {
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                if (pais != null && !pais.isEmpty()) {
                    usuario.setPais(pais);
                }
                usuarioService.actualizarUsuario(usuario);
                model.addAttribute("mensaje", "Configuración actualizada exitosamente");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar configuración: " + e.getMessage());
        }

        return "redirect:/facturacion/configuracion";
    }
}
