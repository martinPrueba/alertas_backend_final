package com.kim21.alertas.service;

import com.kim21.alertas.model.SingularidadesVisibleFieldFilterModel;
import org.springframework.http.ResponseEntity;

public interface VisibleFieldsFilterConfigSingularidadesService
{
    ResponseEntity<?> getAll();
    ResponseEntity<?> create(SingularidadesVisibleFieldFilterModel body);
    ResponseEntity<?> update(Long id, SingularidadesVisibleFieldFilterModel body);
    ResponseEntity<?> delete(Long id);
}
