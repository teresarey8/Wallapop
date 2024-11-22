package org.example.wallapop.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String telefono;
    @NotNull(message = "El email es obligatorio")
    @Column(unique = true, nullable = false)
    private String email;
    @Column(length = 200, nullable = false)
    private String password;
    private String poblacion;
    @OneToMany(mappedBy = "usuario")
    private List<Anuncio> anuncios;
}
