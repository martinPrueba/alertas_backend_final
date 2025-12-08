package com.kim21.alertas.service;

import com.kim21.alertas.dto.SingularidadMarcarLeidaDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SingularidadesService
{
    ResponseEntity<?> findAllSingularidades();

    ResponseEntity<?> findSingularidadById(Integer id);

    ResponseEntity<?> marcarSingularidadComoLeida(SingularidadMarcarLeidaDTO dto);

    ResponseEntity<?> getProcesos();

    ResponseEntity<?> getActivos();

    ResponseEntity<?> filtrarDinamico(Map<String, Object> filtros);

    // Extras
    ResponseEntity<?> getTiposSingularidades();
    ResponseEntity<?> reportSingularidadesDynamic(com.kim21.alertas.dto.SingularidadReportDTO dto);
}
