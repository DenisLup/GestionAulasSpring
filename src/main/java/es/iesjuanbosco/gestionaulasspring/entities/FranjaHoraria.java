package es.iesjuanbosco.gestionaulasspring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString @Builder

@Entity
@Table
public class FranjaHoraria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime horaInicio;
    private LocalTime horaFin;
    private LocalDate dia;

    @OneToMany(mappedBy = "franjaHoraria")
    @JsonIgnore
    private List<Reserva> reservas;

}
