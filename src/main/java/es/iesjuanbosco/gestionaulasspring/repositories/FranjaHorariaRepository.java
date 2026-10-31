package es.iesjuanbosco.gestionaulasspring.repositories;

import es.iesjuanbosco.gestionaulasspring.entities.FranjaHoraria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FranjaHorariaRepository extends JpaRepository<FranjaHoraria, Long> {
}
