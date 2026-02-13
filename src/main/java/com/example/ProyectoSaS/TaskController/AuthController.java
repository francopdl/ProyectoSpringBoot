package com.example.ProyectoSaS.TaskController;

import com.example.ProyectoSaS.Services.PerfilService;
import com.example.ProyectoSaS.Services.SuscripcionService;
import com.example.ProyectoSaS.Services.UsuarioService;
import com.example.ProyectoSaS.enums.TipoPlan;
import com.example.ProyectoSaS.models.Perfil;
import com.example.ProyectoSaS.models.Suscripcion;
import com.example.ProyectoSaS.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SuscripcionService suscripcionService;

    @Autowired
    private PerfilService perfilService;

    @GetMapping
    public String inicio() {
        return "redirect:/login";
    }

    @GetMapping("login")
    public String mostrarLogin(Model model) {
        return "login";
    }

    @PostMapping("login")
    public String procesarLogin(@RequestParam String email, @RequestParam String password, 
                               HttpSession session, Model model) {
        if (usuarioService.validarCredenciales(email, password)) {
            Usuario usuario = usuarioService.buscarPorEmail(email).get();
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("nombreUsuario", usuario.getNombre());
            session.setAttribute("email", usuario.getEmail());
            return "redirect:/dashboard";
        }
        model.addAttribute("error", "Email o contraseña incorrectos");
        return "login";
    }

    @GetMapping("registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("planes", TipoPlan.values());
        return "registro";
    }

    @PostMapping("registro")
    public String procesarRegistro(@RequestParam String nombre,
                                  @RequestParam String apellido,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String tipoPlan,
                                  HttpSession session) {
        try {
           
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setPassword(password);
            
            usuario = usuarioService.registrarUsuario(usuario);

            
            perfilService.crearPerfil(usuario);

          
            TipoPlan plan = TipoPlan.valueOf(tipoPlan);
            suscripcionService.crearSuscripcion(usuario, plan);

            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("nombreUsuario", usuario.getNombre());
            session.setAttribute("email", usuario.getEmail());

            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            return "redirect:/registro?error=" + e.getMessage();
        }
    }

    @RequestMapping(value = "logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
