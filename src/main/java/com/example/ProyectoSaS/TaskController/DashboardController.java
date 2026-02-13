package com.example.ProyectoSaS.TaskController;

import com.example.ProyectoSaS.Services.SuscripcionService;
import com.example.ProyectoSaS.Services.UsuarioService;
import com.example.ProyectoSaS.enums.TipoPlan;
import com.example.ProyectoSaS.models.Suscripcion;
import com.example.ProyectoSaS.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SuscripcionService suscripcionService;

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String nombreUsuario = (String) session.getAttribute("nombreUsuario");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        
        Optional<Suscripcion> suscripcionOpt = suscripcionService.obtenerSuscripcionPorUsuario(usuarioId);
        
        if (suscripcionOpt.isPresent()) {
            Suscripcion suscripcion = suscripcionOpt.get();
            model.addAttribute("nombreUsuario", nombreUsuario);
            model.addAttribute("tipoPlan", suscripcion.getTipoPlan().name());
            model.addAttribute("planDescripcion", suscripcion.getTipoPlan().getDescripcion());
            model.addAttribute("estadoSuscripcion", suscripcion.getEstado());
            model.addAttribute("planes", TipoPlan.values());
            return "dashboard";
        }

        return "redirect:/login";
    }

    @PostMapping("/cambiar-plan")
    public String cambiarPlan(@RequestParam String nuevoTipoPlan,
                             HttpSession session,
                             Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        try {
            TipoPlan plan = TipoPlan.valueOf(nuevoTipoPlan);
            suscripcionService.actualizarPlan(usuarioId, plan);
            model.addAttribute("mensaje", "Plan actualizado correctamente");
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cambiar plan: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }
}
