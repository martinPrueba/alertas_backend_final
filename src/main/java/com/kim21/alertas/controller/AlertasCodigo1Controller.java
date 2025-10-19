package com.kim21.alertas.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kim21.alertas.model.AlertasCodigo1Model;
import com.kim21.alertas.service.AlertasCodigo1Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alertas-codigo1")
@RequiredArgsConstructor
public class AlertasCodigo1Controller 
{

    private final AlertasCodigo1Service alertasCodigo1Service;

    @GetMapping("/list")
    public ResponseEntity<List<AlertasCodigo1Model>> getAll() 
    {
        return ResponseEntity.ok(alertasCodigo1Service.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<AlertasCodigo1Model> create(@RequestBody AlertasCodigo1Model model) {
        return ResponseEntity.ok(alertasCodigo1Service.save(model));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        alertasCodigo1Service.delete(id);
        return ResponseEntity.noContent().build();
    }
}