package es.iesjuanbosco.gestionaulasspring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

//Dependencias de Lombok
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString @Builder
//Dependencias de Sprinboot JPA
@Entity
@Table
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean tieneOrdenadores;
    private Integer capacidad;

    @OneToMany(mappedBy = "aula")
    @JsonIgnore
    private List<Reserva> reservas;
}
