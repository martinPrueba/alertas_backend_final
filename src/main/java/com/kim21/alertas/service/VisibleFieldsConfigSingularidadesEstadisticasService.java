package com.kim21.alertas.service;

import com.kim21.alertas.model.SingularidadEstadisticasVisibleFieldModel;
import org.springframework.http.ResponseEntity;

public interface VisibleFieldsConfigSingularidadesEstadisticasService
{
    ResponseEntity<?> getAll();
    ResponseEntity<?> create(SingularidadEstadisticasVisibleFieldModel body);
    ResponseEntity<?> update(Long id, SingularidadEstadisticasVisibleFieldModel body);
    ResponseEntity<?> delete(Long id);
}
