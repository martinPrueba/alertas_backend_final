package com.kim21.alertas.controller;

import com.kim21.alertas.model.RangoColorConfigModel;
import com.kim21.alertas.service.RangoColorConfigService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rangos-color")
public class RangoColorConfigController {

    private final RangoColorConfigService service;

    public RangoColorConfigController(RangoColorConfigService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody RangoColorConfigModel request) {
        return service.crear(request);
    }

    @PutMapping("/update-all")
    public ResponseEntity<?> actualizar(@RequestBody List<RangoColorConfigModel> request) {
        return service.actualizar(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        return service.eliminar(id);
    }
}
