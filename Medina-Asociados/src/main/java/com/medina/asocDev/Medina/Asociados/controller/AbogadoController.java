package com.medina.asocDev.Medina.Asociados.controller;

import com.medina.asocDev.Medina.Asociados.entity.Abogado;
import com.medina.asocDev.Medina.Asociados.repo.AbogadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

public class AbogadoController {
    @RestController
    @RequestMapping("/api/abogados")
    public class AbogadoController {

        @Autowired
        private AbogadoRepository abogadoRepository;

        @GetMapping
        public List<Abogado> getAllAbogados() {
            return abogadoRepository.findAll();
        }

        @GetMapping("/{id}")
        public ResponseEntity<Abogado> getAbogadoById(@PathVariable Long id) {
            Optional<Abogado> abogado = abogadoRepository.findById(id);
            return abogado.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @PostMapping
        public Abogado createAbogado(@RequestBody Abogado abogado) {
            return abogadoRepository.save(abogado);
        }

        @PutMapping("/{id}")
        public ResponseEntity<Abogado> updateAbogado(@PathVariable Long id, @RequestBody Abogado abogadoDetails) {
            Optional<Abogado> optionalAbogado = abogadoRepository.findById(id);
            if (!optionalAbogado.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            Abogado abogado = optionalAbogado.get();
            abogado.setNombre(abogadoDetails.getNombre());
            abogado.setApellido(abogadoDetails.getApellido());
            abogado.setEspecialidad(abogadoDetails.getEspecialidad());
            // agrega aquí otros campos según tu modelo

            Abogado updatedAbogado = abogadoRepository.save(abogado);
            return ResponseEntity.ok(updatedAbogado);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteAbogado(@PathVariable Long id) {
            if (!abogadoRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            abogadoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
    }

}
