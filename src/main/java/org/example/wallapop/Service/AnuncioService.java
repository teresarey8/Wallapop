package org.example.wallapop.Service;

import org.example.Entity.Anuncio;
import org.example.repository.AnuncioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
