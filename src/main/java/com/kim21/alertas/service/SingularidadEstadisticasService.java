package com.kim21.alertas.service;

import com.kim21.alertas.model.SingularidadesEstadisticasModel;
import org.springframework.http.ResponseEntity;

public interface SingularidadEstadisticasService
{
    ResponseEntity<?> getAll();
    ResponseEntity<?> getById(Integer id);
    ResponseEntity<?> create(SingularidadesEstadisticasModel body);
    ResponseEntity<?> update(Integer id, SingularidadesEstadisticasModel body);
    ResponseEntity<?> delete(Integer id);
}
