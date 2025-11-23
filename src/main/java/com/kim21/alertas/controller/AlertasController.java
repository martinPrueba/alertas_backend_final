package com.kim21.alertas.controller;

import com.kim21.alertas.dto.AlertFilterDTO;
import com.kim21.alertas.dto.AlertMarcarLeidaDTO;
import com.kim21.alertas.dto.AlertReportDTO;
import com.kim21.alertas.service.AlertaConfigServiceImpl;
import com.kim21.alertas.service.AlertasService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertasController 
{

    private final AlertasService alertasService;
          private final AlertaConfigServiceImpl alertaConfigServiceImpl;


    @GetMapping("/get-all-alerts")
    public ResponseEntity<?> getAllAlertas() 
    {
        return alertasService.findAllAlertas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAlertaById(@PathVariable Integer id) 
    {
        return alertasService.findAlertaById(id);
    }



    @PostMapping("/filter-dynamic")
    public ResponseEntity<?> filtrarAlertas(@RequestBody Map<String, Object> filtros) 
    {
        return alertasService.filtrarDinamico(filtros);
    }


    // Endpoint para marcar alerta como leída
    @PostMapping("/marcar-leida")
    public ResponseEntity<?> marcarAlertaComoLeida(@RequestBody AlertMarcarLeidaDTO dto) 
    {
        return alertasService.marcarAlertaComoLeida(dto);
    }

    @GetMapping("/reporte-alertas")
    public ResponseEntity<?> reportAlerts() 
    {
        return alertasService.reportAlerts();
    }

    @GetMapping("/get-procesos")
    public ResponseEntity<?> getProcesos() 
    {
        return alertasService.getProcesos();
    }
    
    @GetMapping("/get-activos")
    public ResponseEntity<?> getActivos() 
    {
        return alertasService.getActivos();
    }


    @GetMapping("get/refresh-interval")
    public ResponseEntity<?> getInterval() 
    {
        return alertaConfigServiceImpl.getRefreshSeconds();
    }

    @PutMapping("post/refresh-interval")
    public ResponseEntity<?> updateInterval(@RequestBody Map<String, Integer> body) 
    {
        Integer seconds = body.get("seconds");
        return alertaConfigServiceImpl.setRefreshSeconds(seconds);
    }

    @GetMapping("/get-alertas-activas")
    public ResponseEntity<?> getAlertasActivas() 
    {
        return alertasService.getAlertasActivas();
    }

    @GetMapping("/get-tipos")
    public ResponseEntity<?> getTipos()
    {
        return alertasService.getTipos();
    }

    @PostMapping("/reporte-alertas-dynamic")
    public ResponseEntity<?> reportAlertsDynamic( @RequestBody AlertReportDTO dto) 
    {
        return alertasService.reportAlertsDynamic(dto);
    }


    @GetMapping("/getall-usergrupos-locales")
    public ResponseEntity<?> getAllUserGruposLocales() 
    {
        return alertasService.getAllUserGruposLocales();
    }
    

}