package es.iesjuanbosco.gestionaulasspring.controllers;

import es.iesjuanbosco.gestionaulasspring.entities.Aula;
import es.iesjuanbosco.gestionaulasspring.repositories.AulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AulaController {

    @Autowired //Inyección de dependencias
    private AulaRepository aulaRepository;

    //GET -> http://localhost:8080/aulas/id
    @GetMapping("/aulas/{id}")
    public ResponseEntity<Aula> findById(@PathVariable Long id){
        return aulaRepository.findById(id).map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.notFound().build()); //404 Not Found
    }
    //GET -> http://localhost:8080/aulas?tieneOrdenadores=true
    @GetMapping(value = "/aulas")
    public ResponseEntity<List<Aula>> findByTieneOrdenadores(
            @RequestParam(name = "tieneOrdenadores", required = false) Boolean tieneOrdenadores){
        List<Aula> aulas = tieneOrdenadores == null
                ? aulaRepository.findAll()
                : aulaRepository.findByTieneOrdenadores(tieneOrdenadores);
        return ResponseEntity.ok(aulas);
    }
    //GET -> http://localhost:8080/aulas?capacidad=num
    @GetMapping(value = "/aulas", params = "capacidad")
    public ResponseEntity<List<Aula>> findByCapacidad(
            @RequestParam(name = "capacidad") Integer capacidad){
        List<Aula> aulas = aulaRepository.findByCapacidad(capacidad);
        return ResponseEntity.ok(aulas);
    }

    //DELETE -> http://localhost:8080/aulas/id
    @DeleteMapping("/aulas/{id}")
    public ResponseEntity<Aula> deleteById(@PathVariable Long id){
        aulaRepository.deleteById(id);
        return ResponseEntity.notFound().build(); //204 -> No Content
    }

    //POST -> http://localhost:8080/aulas
    @PostMapping("/aulas")
    public ResponseEntity<Aula> create(@RequestBody Aula aula){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aulaRepository.save(aula));
    }
    //PUT -> http://localhost:8080/aulas/id
    @PutMapping("/aulas/{id}")
    public ResponseEntity<Aula> update(@PathVariable Long id, @RequestBody Aula aula){
        return aulaRepository.findById(id).map(a -> {
            a.setId(id);
            return ResponseEntity.ok(aulaRepository.save(aula)); //Si existe 200 OK
        }).orElseGet(()-> ResponseEntity.notFound().build());// Si no 404 Not Found
    }
}
