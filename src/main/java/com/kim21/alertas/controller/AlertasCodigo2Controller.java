package com.kim21.alertas.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kim21.alertas.model.AlertasCodigo2Model;
import com.kim21.alertas.service.AlertasCodigo2Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alertas-codigo2")
@RequiredArgsConstructor
public class AlertasCodigo2Controller 
{

    private final AlertasCodigo2Service alertasCodigo2Service;

    @GetMapping("/list")
    public ResponseEntity<List<AlertasCodigo2Model>> getAll() {
        return ResponseEntity.ok(alertasCodigo2Service.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<AlertasCodigo2Model> create(@RequestBody AlertasCodigo2Model model) {
        return ResponseEntity.ok(alertasCodigo2Service.save(model));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        alertasCodigo2Service.delete(id);
        return ResponseEntity.noContent().build();
    }
}