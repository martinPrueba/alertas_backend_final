package com.kim21.alertas.service;

import com.kim21.alertas.model.RangoColorConfigModel;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface RangoColorConfigService {
    ResponseEntity<?> listar();
    ResponseEntity<?> obtenerPorId(Integer id);
    ResponseEntity<?> crear(RangoColorConfigModel request);
    ResponseEntity<?> actualizar(List<RangoColorConfigModel> request);
    ResponseEntity<?> eliminar(Integer id);
}
