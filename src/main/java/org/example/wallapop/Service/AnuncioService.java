package org.example.wallapop.Service;



import org.example.wallapop.Entity.Anuncio;
import org.example.wallapop.Entity.Usuario;
import org.example.wallapop.repository.AnuncioRepository;
import org.example.wallapop.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    // Constructor
    @Autowired
    public AnuncioService(AnuncioRepository anuncioRepository, UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.anuncioRepository = anuncioRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    // Métoodo para obtener todos los anuncios
    public List<Anuncio> obtenerTodosAnuncios() {
        return anuncioRepository.findAllByOrderByFechaCreacionDesc();
    }

    public void saveAnuncio(Anuncio anuncio) {
        if (anuncio.getFechaCreacion() == null) {
            anuncio.setFechaCreacion(LocalDate.now());
        }
        anuncioRepository.save(anuncio);
    }

    public Optional<Anuncio> findAnuncioById(Long id) {
        return anuncioRepository.findById(id);
    }

    public void deleteAnuncioById(Long id) {
        anuncioRepository.deleteById(id);
    }

    public void guardarAnuncio(Anuncio anuncio, Long usuarioId) {
        if (usuarioId == 0) {
            throw new RuntimeException("ID de Usuario no puede ser 0");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + usuarioId + " no encontrado"));

        anuncio.setUsuario(usuario);
        anuncioRepository.save(anuncio);
    }
    public List<Anuncio> obtenerAnunciosPorUsuario(String email) {
        return anuncioRepository.findByUsuarioEmail(email);  // Método personalizado
    }
    public boolean esPropietario(Long anuncioId, Long userId) {
        Optional<Anuncio> anuncio = anuncioRepository.findById(anuncioId);
        return anuncio.isPresent() && anuncio.get().getUsuario().getId().equals(userId);
    }
    public void editarAnuncio(Long id, Anuncio anuncioActualizado, Usuario usuarioAutenticado) {
        // Busca el anuncio por ID
        Anuncio anuncioExistente = anuncioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anuncio no encontrado"));

        // Verifica que el usuario autenticado sea el propietario
        if (!anuncioExistente.getUsuario().getEmail().equals(usuarioAutenticado.getEmail())) {
            throw new RuntimeException("No tienes permiso para editar este anuncio");
        }

        // Actualiza los campos del anuncio
        anuncioExistente.setTitulo(anuncioActualizado.getTitulo());
        anuncioExistente.setDescripcion(anuncioActualizado.getDescripcion());
        anuncioExistente.setPrecio(anuncioActualizado.getPrecio());
        // Agrega más campos si es necesario

        // Guarda los cambios
        anuncioRepository.save(anuncioExistente);
    }

}
