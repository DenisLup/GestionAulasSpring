package es.iesjuanbosco.gestionaulasspring.repositories;

import es.iesjuanbosco.gestionaulasspring.entities.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula,Long> {
    List<Aula> findByTieneOrdenadores(boolean tieneOrdenadores);
    List<Aula> findByCapacidad(int capacidad);
}
