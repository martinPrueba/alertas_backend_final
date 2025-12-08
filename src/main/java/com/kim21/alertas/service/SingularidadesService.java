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

    // Visible fields config for singularidades estadísticas
    ResponseEntity<?> getAllSingularidadEstadisticasVisibleFields();
    ResponseEntity<?> createSingularidadEstadisticasVisibleField(com.kim21.alertas.model.SingularidadEstadisticasVisibleFieldModel body);
    ResponseEntity<?> updateSingularidadEstadisticasVisibleField(Long id, com.kim21.alertas.model.SingularidadEstadisticasVisibleFieldModel body);
    ResponseEntity<?> deleteSingularidadEstadisticasVisibleField(Long id);

    // Visible fields filter config
    ResponseEntity<?> getAllSingularidadesVisibleFieldFilters();
    ResponseEntity<?> createSingularidadesVisibleFieldFilter(com.kim21.alertas.model.SingularidadesVisibleFieldFilterModel body);
    ResponseEntity<?> updateSingularidadesVisibleFieldFilter(Long id, com.kim21.alertas.model.SingularidadesVisibleFieldFilterModel body);
    ResponseEntity<?> deleteSingularidadesVisibleFieldFilter(Long id);

    // CRUD singularidadesestadisticas
    ResponseEntity<?> getAllSingularidadesEstadisticas();
    ResponseEntity<?> getSingularidadesEstadisticasById(Integer id);
    ResponseEntity<?> createSingularidadesEstadisticas(com.kim21.alertas.model.SingularidadesEstadisticasModel body);
    ResponseEntity<?> updateSingularidadesEstadisticas(Integer id, com.kim21.alertas.model.SingularidadesEstadisticasModel body);
    ResponseEntity<?> deleteSingularidadesEstadisticas(Integer id);

    // Extras
    ResponseEntity<?> getTiposSingularidades();
    ResponseEntity<?> reportSingularidadesDynamic(com.kim21.alertas.dto.SingularidadReportDTO dto);
}
