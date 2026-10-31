package es.iesjuanbosco.gestionaulasspring.repositories;

import es.iesjuanbosco.gestionaulasspring.entities.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    List<Profesor> findByJefeEstudios(boolean jefeEstudios);
    List<Profesor> findByDirector(boolean director);
    boolean existsByDirectorTrue();
    boolean existsByJefeEstudiosTrue();
}
