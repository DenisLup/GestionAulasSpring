package es.iesjuanbosco.gestionaulasspring.controllers;

import es.iesjuanbosco.gestionaulasspring.entities.Reserva;
import es.iesjuanbosco.gestionaulasspring.repositories.FranjaHorariaRepository;
import es.iesjuanbosco.gestionaulasspring.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private FranjaHorariaRepository franjaHorariaRepository;

    // GET -> http://localhost:8080/reservas/{id}
    @GetMapping("/reservas/{id}")
    public ResponseEntity<Reserva> findById(@PathVariable Long id) {
        return reservaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET -> http://localhost:8080/reservas
    @GetMapping("/reservas")
    public ResponseEntity<List<Reserva>> findAll() {
        return ResponseEntity.ok(reservaRepository.findAll());
    }

    // POST -> http://localhost:8080/reservas
    @PostMapping("/reservas")
    public ResponseEntity<Reserva> create(@RequestBody Reserva reserva) {
        if (reserva.getFranjaHoraria() == null || reserva.getFranjaHoraria().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        var franjaHorariaOptional = franjaHorariaRepository.findById(reserva.getFranjaHoraria().getId());
        if (franjaHorariaOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var franjaHoraria = franjaHorariaOptional.get();
        if (franjaHoraria.getDia() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (franjaHoraria.getDia().isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().build();
        }
        if (reservaRepository.existsByFranjaHoraria_Id(franjaHoraria.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        reserva.setFranjaHoraria(franjaHoraria);
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(reservaRepository.save(reserva));
        } catch (DataIntegrityViolationException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // PUT -> http://localhost:8080/reservas/{id}
    @PutMapping("/reservas/{id}")
    public ResponseEntity<Reserva> update(@PathVariable Long id, @RequestBody Reserva reserva) {
        return reservaRepository.findById(id).map(reservaExistente -> {
            reservaExistente.setProfesor(reserva.getProfesor());
            reservaExistente.setAula(reserva.getAula());
            reservaExistente.setFranjaHoraria(reserva.getFranjaHoraria());
            return ResponseEntity.ok(reservaRepository.save(reservaExistente));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE -> http://localhost:8080/reservas/{id}
    @DeleteMapping("/reservas/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!reservaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reservaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
