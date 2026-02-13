package com.example.ProyectoSaS.TaskController;

import com.example.ProyectoSaS.Services.FacturaService;
import com.example.ProyectoSaS.Services.UsuarioService;
import com.example.ProyectoSaS.models.Factura;
import com.example.ProyectoSaS.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/auditoria")
public class AuditoriaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String mostrarAuditoria(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/login";
        }

        List<Factura> todasLasFacturas = facturaService.obtenerTodasLasFacturas();
        
        model.addAttribute("totalFacturas", todasLasFacturas.size());
        model.addAttribute("facturas", todasLasFacturas);
        
        long facturasActivas = todasLasFacturas.stream()
            .filter(f -> "PAGADA".equals(f.getEstado()))
            .count();
        
        long facturasPendientes = todasLasFacturas.stream()
            .filter(f -> "PENDIENTE".equals(f.getEstado()))
            .count();

        model.addAttribute("facturasActivas", facturasActivas);
        model.addAttribute("facturasPendientes", facturasPendientes);
        model.addAttribute("fechaPantalla", LocalDateTime.now());

        return "auditoria";
    }

    @PostMapping("/filtrar-fechas")
    public String filtrarPorFechas(
            @RequestParam(value = "fechaInicio", required = true) String fechaInicio,
            @RequestParam(value = "fechaFin", required = true) String fechaFin,
            HttpSession session, Model model) {
        
        if (!esAdmin(session)) {
            return "redirect:/login";
        }

        try {
            LocalDate fechaInicioParsed = LocalDate.parse(fechaInicio.substring(0, 10));
            LocalDate fechaFinParsed = LocalDate.parse(fechaFin.substring(0, 10));
            
            LocalDateTime inicio = LocalDateTime.of(fechaInicioParsed, LocalTime.of(0, 0, 0));
            LocalDateTime fin = LocalDateTime.of(fechaFinParsed, LocalTime.of(23, 59, 59));
            
            List<Factura> todasLasFacturas = facturaService.obtenerTodasLasFacturas();
            
            List<Factura> facturasFiltradas = todasLasFacturas.stream()
                .filter(f -> f.getFechaEmision().isAfter(inicio) && f.getFechaEmision().isBefore(fin))
                .toList();

            model.addAttribute("facturas", facturasFiltradas);
            model.addAttribute("totalFacturas", facturasFiltradas.size());
            model.addAttribute("filtroAplicado", "Auditoría de " + fechaInicio + " a " + fechaFin);
            
            long facturasActivas = facturasFiltradas.stream()
                .filter(f -> "PAGADA".equals(f.getEstado()))
                .count();
            
            long facturasPendientes = facturasFiltradas.stream()
                .filter(f -> "PENDIENTE".equals(f.getEstado()))
                .count();

            model.addAttribute("facturasActivas", facturasActivas);
            model.addAttribute("facturasPendientes", facturasPendientes);
            model.addAttribute("fechaPantalla", LocalDateTime.now());

        } catch (Exception e) {
            model.addAttribute("error", "Error al filtrar registros de auditoría");
        }

        return "auditoria";
    }

    @PostMapping("/filtrar-operacion")
    public String filtrarPorOperacion(
            @RequestParam(value = "operacion", required = true) String operacion,
            HttpSession session, Model model) {
        
        if (!esAdmin(session)) {
            return "redirect:/login";
        }

        List<Factura> todasLasFacturas = facturaService.obtenerTodasLasFacturas();
        
        List<Factura> facturasFiltradas = todasLasFacturas.stream()
            .filter(f -> operacion.equalsIgnoreCase(f.getEstado()))
            .toList();

        long facturasActivas = facturasFiltradas.stream()
            .filter(f -> "PAGADA".equals(f.getEstado()))
            .count();
        
        long facturasPendientes = facturasFiltradas.stream()
            .filter(f -> "PENDIENTE".equals(f.getEstado()))
            .count();

        model.addAttribute("facturas", facturasFiltradas);
        model.addAttribute("totalFacturas", facturasFiltradas.size());
        model.addAttribute("filtroAplicado", "Auditoría - Operación: " + operacion);
        model.addAttribute("facturasActivas", facturasActivas);
        model.addAttribute("facturasPendientes", facturasPendientes);
        model.addAttribute("fechaPantalla", LocalDateTime.now());

        return "auditoria";
    }

    @GetMapping("/factura/{facturaId}")
    public String verDetalleAuditoria(
            @PathVariable Long facturaId,
            HttpSession session, Model model) {
        
        if (!esAdmin(session)) {
            return "redirect:/login";
        }

        Optional<Factura> facturaOpt = facturaService.obtenerTodasLasFacturas()
            .stream()
            .filter(f -> f.getId().equals(facturaId))
            .findFirst();

        if (facturaOpt.isEmpty()) {
            model.addAttribute("error", "Factura no encontrada en auditoría");
            return "redirect:/admin/auditoria";
        }

        Factura factura = facturaOpt.get();
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(factura.getUsuario().getId());

        model.addAttribute("factura", factura);
        model.addAttribute("usuario", usuarioOpt.get());
        
        model.addAttribute("fechaEmision", factura.getFechaEmision());
        model.addAttribute("fechaVencimiento", factura.getFechaVencimiento());
        model.addAttribute("estado", factura.getEstado());

        return "detalle-auditoria";
    }

    @GetMapping("/usuario/{usuarioId}")
    public String verHistorialUsuario(
            @PathVariable Long usuarioId,
            HttpSession session, Model model) {
        
        if (!esAdmin(session)) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);
        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/auditoria";
        }

        Usuario usuario = usuarioOpt.get();
        List<Factura> facturasUsuario = facturaService.obtenerFacturasPorUsuario(usuarioId);

        model.addAttribute("usuario", usuario);
        model.addAttribute("facturas", facturasUsuario);
        model.addAttribute("totalFacturasUsuario", facturasUsuario.size());
        
        long facturasPagadas = facturasUsuario.stream()
            .filter(f -> "PAGADA".equals(f.getEstado()))
            .count();

        model.addAttribute("facturasPagadas", facturasPagadas);

        return "historial-usuario";
    }

    @GetMapping("/reporte")
    public String generarReporte(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/login";
        }

        List<Factura> todasLasFacturas = facturaService.obtenerTodasLasFacturas();
        
        model.addAttribute("totalFacturas", todasLasFacturas.size());
        
        long facturasActivas = todasLasFacturas.stream()
            .filter(f -> "PAGADA".equals(f.getEstado()))
            .count();
        
        long facturasPendientes = todasLasFacturas.stream()
            .filter(f -> "PENDIENTE".equals(f.getEstado()))
            .count();

        model.addAttribute("facturasActivas", facturasActivas);
        model.addAttribute("facturasPendientes", facturasPendientes);
        model.addAttribute("fechaGeneracion", LocalDateTime.now());

        return "reporte-auditoria";
    }

    private boolean esAdmin(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        return usuarioId != null;
    }
}
