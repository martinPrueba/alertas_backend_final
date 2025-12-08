package com.kim21.alertas.controller;

import com.kim21.alertas.dto.SingularidadMarcarLeidaDTO;
import com.kim21.alertas.dto.SingularidadReportDTO;
import com.kim21.alertas.service.SingularidadesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/singularidades")
@RequiredArgsConstructor
public class SingularidadesController
{

    private final SingularidadesService singularidadesService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllSingularidades()
    {
        return singularidadesService.findAllSingularidades();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSingularidadById(@PathVariable Integer id)
    {
        return singularidadesService.findSingularidadById(id);
    }


    @PostMapping("/marcar-leida")
    public ResponseEntity<?> marcarSingularidadComoLeida(@RequestBody SingularidadMarcarLeidaDTO dto)
    {
        return singularidadesService.marcarSingularidadComoLeida(dto);
    }

    @GetMapping("/get-procesos")
    public ResponseEntity<?> getProcesos()
    {
        return singularidadesService.getProcesos();
    }

    @GetMapping("/get-activos")
    public ResponseEntity<?> getActivos()
    {
        return singularidadesService.getActivos();
    }

    @PostMapping("/filter-dynamic")
    public ResponseEntity<?> filtrarSingularidades(@RequestBody Map<String, Object> filtros)
    {
        return singularidadesService.filtrarDinamico(filtros);
    }

    @GetMapping("/get-tipos")
    public ResponseEntity<?> getTiposSingularidades()
    {
        return singularidadesService.getTiposSingularidades();
    }

    @PostMapping("/reporte-singularidades-dynamic")
    public ResponseEntity<?> reportSingularidadesDynamic(@RequestBody SingularidadReportDTO dto)
    {
        return singularidadesService.reportSingularidadesDynamic(dto);
    }
}
