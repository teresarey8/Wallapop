package org.example.wallapop.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name="anuncios")
public class Anuncio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //el titulo y el precio son campos obligatorios
    @NotEmpty(message = "El titulo del anuncio no puede estar en blanco")
    @Column(length = 200)
    private String titulo;
    @NotNull
    //para que sea positivo, y el precio no puede tener notempty ese es para string
    @Positive
    private Double precio;
    //la descripcion puede tener maximo 500 caracteres
    @Column(length = 500)
    //el lob es una entidad de anotacion para las textarea, que es lo que considero que es una descripcion
    @Lob
    private String descripcion;
    private LocalDate fechaCreacion;

    @OneToMany(targetEntity = FotoAnuncio.class, cascade = CascadeType.ALL, mappedBy = "anuncio", orphanRemoval = true)
    @ToString.Exclude
    private List<FotoAnuncio> fotos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "usuario", nullable = false) // Nombre de la columna en la base de datos
    private Usuario usuario;

    public FotoAnuncio getPrimeraFoto(){
        FotoAnuncio fotoAnuncio = new FotoAnuncio();
        fotoAnuncio.setNombre("/default.png");
        if(!fotos.isEmpty()){
            return  fotos.get(0);
        }else{
            return fotoAnuncio;
        }
    }
    @ManyToMany
    @JoinTable(
            name = "anuncio_categoria",
            joinColumns = @JoinColumn(name = "anuncio_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<Categoria> categorias;
}











