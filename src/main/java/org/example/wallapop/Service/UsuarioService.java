package org.example.wallapop.Service;

import org.example.wallapop.Entity.Usuario;
import org.example.wallapop.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario registrarUsuario(Usuario usuario) {
        // Asegúrate de que el usuario no tenga un ID predefinido
        // BCryptPasswordEncoder encripta la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario); // La base de datos asignará el ID
    }

    public Optional<Usuario> buscarPorId(Long id) {
        if (id == 0) {
            throw new RuntimeException("ID de Usuario no puede ser 0");
        }
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}
