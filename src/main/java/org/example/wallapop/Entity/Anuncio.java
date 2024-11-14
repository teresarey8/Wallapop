package org.example.wallapop.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    @NotEmpty(message = "El precio no puede estar en blanco")
    @Min(value = 0, message = "El precio tiene que ser positivo")
    private Double precio;
    //la descripcion puede tener maximo 500 caracteres
    @Column(length = 500)
    //el lob es una entidad de anotacion para las textarea, que es lo que considero que es una descripcion
    @Lob
    private String descripcion;
    private LocalDate fechaCreacion;
    private String Foto;

    @OneToMany(targetEntity = FotoAnuncio.class, cascade = CascadeType.ALL, mappedBy = "anuncio")
    private List<FotoAnuncio> fotos = new ArrayList<>();

}











