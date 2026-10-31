package es.iesjuanbosco.gestionaulasspring.controllers;

import es.iesjuanbosco.gestionaulasspring.entities.FranjaHoraria;
import es.iesjuanbosco.gestionaulasspring.repositories.FranjaHorariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FranjaHorariaController {

    @Autowired
    private FranjaHorariaRepository franjaHorariaRepository;

    // GET -> http://localhost:8080/franjashorarias/{id}
    @GetMapping("/franjashorarias/{id}")
    public ResponseEntity<FranjaHoraria> findById(@PathVariable Long id) {
        return franjaHorariaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET -> http://localhost:8080/franjashorarias
    @GetMapping("/franjashorarias")
    public ResponseEntity<List<FranjaHoraria>> findAll() {
        return ResponseEntity.ok(franjaHorariaRepository.findAll());
    }

    // POST -> http://localhost:8080/franjashorarias
    @PostMapping("/franjashorarias")
    public ResponseEntity<FranjaHoraria> create(@RequestBody FranjaHoraria franjaHoraria) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(franjaHorariaRepository.save(franjaHoraria));
    }

    // PUT -> http://localhost:8080/franjashorarias/{id}
    @PutMapping("/franjashorarias/{id}")
    public ResponseEntity<FranjaHoraria> update(
            @PathVariable Long id, @RequestBody FranjaHoraria franjaHoraria) {
        return franjaHorariaRepository.findById(id).map(franjaExistente -> {
            franjaExistente.setHoraInicio(franjaHoraria.getHoraInicio());
            franjaExistente.setHoraFin(franjaHoraria.getHoraFin());
            franjaExistente.setDia(franjaHoraria.getDia());
            return ResponseEntity.ok(franjaHorariaRepository.save(franjaExistente));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE -> http://localhost:8080/franjashorarias/{id}
    @DeleteMapping("/franjashorarias/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!franjaHorariaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        franjaHorariaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
