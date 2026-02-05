package com.example.ProyectoSaS.Services;

import com.example.ProyectoSaS.models.Perfil;
import com.example.ProyectoSaS.models.Usuario;
import com.example.ProyectoSaS.repositories.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PerfilService {
    @Autowired
    private PerfilRepository perfilRepository;

    public Perfil crearPerfil(Usuario usuario) {
        Perfil perfil = new Perfil();
        perfil.setUsuario(usuario);
        return perfilRepository.save(perfil);
    }

    public Optional<Perfil> obtenerPerfilPorUsuario(Long usuarioId) {
        return perfilRepository.findByUsuarioId(usuarioId);
    }

    public Perfil actualizarPerfil(Perfil perfil) {
        return perfilRepository.save(perfil);
    }
}
