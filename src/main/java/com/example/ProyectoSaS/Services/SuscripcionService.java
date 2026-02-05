package com.example.ProyectoSaS.Services;

import com.example.ProyectoSaS.enums.EstadoSuscripcion;
import com.example.ProyectoSaS.enums.TipoPlan;
import com.example.ProyectoSaS.models.Suscripcion;
import com.example.ProyectoSaS.models.Usuario;
import com.example.ProyectoSaS.repositories.SuscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class SuscripcionService {
    @Autowired
    private SuscripcionRepository suscripcionRepository;

    public Suscripcion crearSuscripcion(Usuario usuario, TipoPlan tipoPlan) {
        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setUsuario(usuario);
        suscripcion.setTipoPlan(tipoPlan);
        suscripcion.setEstado(EstadoSuscripcion.ACTIVA);
        return suscripcionRepository.save(suscripcion);
    }

    public Optional<Suscripcion> obtenerSuscripcionPorUsuario(Long usuarioId) {
        return suscripcionRepository.findByUsuarioId(usuarioId);
    }

    public Suscripcion actualizarPlan(Long usuarioId, TipoPlan nuevoTipoPlan) {
        Optional<Suscripcion> suscripcionOpt = suscripcionRepository.findByUsuarioId(usuarioId);
        if (suscripcionOpt.isPresent()) {
            Suscripcion suscripcion = suscripcionOpt.get();
            suscripcion.setTipoPlan(nuevoTipoPlan);
            return suscripcionRepository.save(suscripcion);
        }
        throw new RuntimeException("Suscripción no encontrada");
    }

    public void cancelarSuscripcion(Long usuarioId) {
        Optional<Suscripcion> suscripcionOpt = suscripcionRepository.findByUsuarioId(usuarioId);
        if (suscripcionOpt.isPresent()) {
            Suscripcion suscripcion = suscripcionOpt.get();
            suscripcion.setEstado(EstadoSuscripcion.CANCELADA);
            suscripcionRepository.save(suscripcion);
        }
    }
}
