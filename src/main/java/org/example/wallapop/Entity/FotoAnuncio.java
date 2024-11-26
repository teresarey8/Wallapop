package org.example.wallapop.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "fotosAnuncios")
public class FotoAnuncio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "anuncio_id", nullable = false) // Asegúrate de que este nombre coincide con el de la base de datos
    private Anuncio anuncio;


}
