package com.example.ProyectoSaS.config;

import com.example.ProyectoSaS.models.Plan;
import com.example.ProyectoSaS.repositories.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PlanRepository planRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear planes si no existen
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
    }
}
