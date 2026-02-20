package com.example.ProyectoSaS.config;

import com.example.ProyectoSaS.models.*;
import com.example.ProyectoSaS.repositories.*;
import com.example.ProyectoSaS.enums.EstadoSuscripcion;
import com.example.ProyectoSaS.enums.TipoPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PlanRepository planRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PerfilRepository perfilRepository;
    
    @Autowired
    private SuscripcionRepository suscripcionRepository;
    
    @Autowired
    private FacturaRepository facturaRepository;

    @Override
    public void run(String... args) throws Exception {
        if (planRepository.count() == 0) {
            Plan planBasic = new Plan();
            planBasic.setNombre("BASIC");
            planBasic.setPrecio(9.99);
            planBasic.setDescripcion("Plan Básico");
            planBasic.setLimiteUsuarios(1);
            planBasic.setLimiteProyectos(3);
            planRepository.save(planBasic);

            Plan planPremium = new Plan();
            planPremium.setNombre("PREMIUM");
            planPremium.setPrecio(29.99);
            planPremium.setDescripcion("Plan Premium");
            planPremium.setLimiteUsuarios(5);
            planPremium.setLimiteProyectos(10);
            planRepository.save(planPremium);

            Plan planEnterprise = new Plan();
            planEnterprise.setNombre("ENTERPRISE");
            planEnterprise.setPrecio(99.99);
            planEnterprise.setDescripcion("Plan Enterprise");
            planEnterprise.setLimiteUsuarios(50);
            planEnterprise.setLimiteProyectos(100);
            planRepository.save(planEnterprise);

            System.out.println("Planes iniciales creados exitosamente");
        }
        
        if (usuarioRepository.count() == 0 || facturaRepository.count() < 5) {
            // Clear incomplete data if detected
            if (usuarioRepository.count() > 0 && facturaRepository.count() < 5) {
                facturaRepository.deleteAll();
                suscripcionRepository.deleteAll();
                perfilRepository.deleteAll();
                usuarioRepository.deleteAll();
                System.out.println("Datos incompletos detectados, se reinicializan");
            }
            
            Usuario usuario = new Usuario();
            usuario.setNombre("Franco");
            usuario.setApellido("García");
            usuario.setEmail("franco@example.com");
            usuario.setPassword("password123");
            usuario.setPais("ES");
            usuario = usuarioRepository.save(usuario);

            Perfil perfil = new Perfil();
            perfil.setUsuario(usuario);
            perfilRepository.save(perfil);

            Plan planPremium = planRepository.findAll().stream()
                .filter(p -> "PREMIUM".equals(p.getNombre()))
                .findFirst()
                .orElse(planRepository.findAll().get(0));
            
            Suscripcion suscripcion = new Suscripcion();
            suscripcion.setUsuario(usuario);
            suscripcion.setTipoPlan(TipoPlan.PREMIUM);
            suscripcion.setEstado(EstadoSuscripcion.ACTIVA);
            suscripcion.setFechaInicio(LocalDateTime.now().minusDays(30));
            suscripcion = suscripcionRepository.save(suscripcion);

            for (int i = 1; i <= 5; i++) {
                Factura factura = new Factura();
                factura.setUsuario(usuario);
                factura.setSuscripcion(suscripcion);
                factura.setNumeroFactura("FAC-2026-" + String.format("%04d", i));
                factura.setFechaEmision(LocalDateTime.now().minusDays(30 - i * 5));
                factura.setFechaVencimiento(LocalDateTime.now().minusDays(30 - i * 5).plusDays(30));
                factura.setMonto(new BigDecimal("100.00"));
                factura.setPorcentajeImpuesto(new BigDecimal("21"));
                factura.setMontoImpuesto(new BigDecimal("21.00"));
                factura.setTotalConImpuesto(new BigDecimal("121.00"));
                factura.setEstado(i <= 2 ? "PAGADA" : "PENDIENTE");
                facturaRepository.save(factura);
            }

            System.out.println("Datos de prueba creados: Usuario, perfil, suscripción y facturas");
        }
    }
}
