package es.iesjuanbosco.gestionaulasspring.controllers;

import es.iesjuanbosco.gestionaulasspring.entities.Profesor;
import es.iesjuanbosco.gestionaulasspring.repositories.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProfesorController {

    @Autowired
    private ProfesorRepository profesorRepository;

    // GET -> http://localhost:8080/profesores/{id}
    @GetMapping("/profesores/{id}")
    public ResponseEntity<Profesor> findById(@PathVariable Long id) {
        return profesorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET -> http://localhost:8080/profesores
    @GetMapping("/profesores")
    public ResponseEntity<List<Profesor>> findAll() {
        return ResponseEntity.ok(profesorRepository.findAll());
    }

    //GET -> http://localhost:8080/profesores?jefeEstudios=true
    @GetMapping(value = "/profesores", params = "jefeEstudios")
    public ResponseEntity<List<Profesor>> findByJefeEstudios(
            @RequestParam(name = "jefeEstudios") boolean jefeEstudios) {
        return ResponseEntity.ok(profesorRepository.findByJefeEstudios(jefeEstudios));
    }
    //GET -> http://localhost:8080/profesores?director=true
    @GetMapping(value = "/profesores", params = "director")
    public ResponseEntity<List<Profesor>> findByDirector(
            @RequestParam(name = "director") boolean director) {
        return ResponseEntity.ok(profesorRepository.findByDirector(director));
    }

    // DELETE -> http://localhost:8080/profesores/{id}
    @DeleteMapping("/profesores/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!profesorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        profesorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // POST -> http://localhost:8080/profesores
    @PostMapping("/profesores")
    public ResponseEntity<Profesor> create(@RequestBody Profesor profesor) {
        if (profesor.isDirector() && profesor.isJefeEstudios()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Un profesor no puede ser director y jefe de estudios a la vez.");
        }
        if (profesor.isDirector() && profesorRepository.existsByDirectorTrue()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un director. No se puede crear otro.");
        }
        if (profesor.isJefeEstudios() && profesorRepository.existsByJefeEstudiosTrue()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un jefe de estudios. No se puede crear otro.");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profesorRepository.save(profesor));
    }

    // PUT -> http://localhost:8080/profesores/{id}
    @PutMapping("/profesores/{id}")
    public ResponseEntity<Profesor> update(@PathVariable Long id, @RequestBody Profesor profesor) {
        return profesorRepository.findById(id).map(profesorExistente -> {
            profesorExistente.setNombre(profesor.getNombre());
            profesorExistente.setAsignaturas(profesor.getAsignaturas());
            profesorExistente.setDirector(profesor.isDirector());
            profesorExistente.setJefeEstudios(profesor.isJefeEstudios());
            return ResponseEntity.ok(profesorRepository.save(profesorExistente));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
