package es.iesjuanbosco.gestionaulasspring.entities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString @Builder

@Entity
@Table
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Profesor profesor;

    @ManyToOne
    private Aula aula;

    @ManyToOne
    @JoinColumn(name = "franja_horaria_id", unique = true)
    private FranjaHoraria franjaHoraria;
}
