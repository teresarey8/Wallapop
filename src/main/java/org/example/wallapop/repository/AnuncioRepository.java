package org.example.wallapop.repository;

import org.example.wallapop.Entity.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
    List<Anuncio> findAllByOrderByFechaCreacionDesc();
    //List<Anuncio> findByUsuario(Usuario usuario);
}
