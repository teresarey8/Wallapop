package org.example.wallapop.Service;



import org.example.wallapop.Entity.Anuncio;
import org.example.wallapop.repository.AnuncioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;


    // Constructor
    @Autowired
    public AnuncioService(AnuncioRepository anuncioRepository) {
        this.anuncioRepository = anuncioRepository;
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

    /*public List<Anuncio> obtenerAnunciosPorUsuario(Usuario usuario) {
        return anuncioRepository.findByUsuario(usuario);
    }*/

}
