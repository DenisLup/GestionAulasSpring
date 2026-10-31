package es.iesjuanbosco.gestionaulasspring.repositories;

import es.iesjuanbosco.gestionaulasspring.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsByFranjaHoraria_Id(Long franjaHorariaId);
}
