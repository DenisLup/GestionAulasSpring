package es.iesjuanbosco.gestionaulasspring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString @Builder

@Entity
@Table
public class Profesor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    @ElementCollection
    private List<String> asignaturas;
    private boolean director;
    private boolean jefeEstudios;

    @OneToMany(mappedBy = "profesor")
    @JsonIgnore
    private List<Reserva> reservas;
}
