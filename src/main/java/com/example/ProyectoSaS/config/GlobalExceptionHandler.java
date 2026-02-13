package com.example.ProyectoSaS.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model, HttpSession session) {
        logger.error("Error no manejado: ", e);
        
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("error", "Ha ocurrido un error: " + e.getMessage());
        model.addAttribute("detalleError", e.toString());
        
        return "error";
    }
}
