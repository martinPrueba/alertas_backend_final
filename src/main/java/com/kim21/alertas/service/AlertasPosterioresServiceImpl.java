package com.kim21.alertas.service;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kim21.alertas.model.AlertasPosterioresModel;
import com.kim21.alertas.model.VisibleFieldConfigModel;
import com.kim21.alertas.repository.AlertasPosterioresRepository;
import com.kim21.alertas.repository.VisibleFieldConfigRepository;


@Service
public class AlertasPosterioresServiceImpl implements AlertasPosterioresService 
{
        
    @Autowired
    private AlertasPosterioresRepository posterioresRepository;
    
    @Autowired
    private VisibleFieldConfigRepository visibleFieldConfigRepository;

    private static final Map<String, String> COLUMN_TO_FIELD = Map.of(
    "fecha_reconocimiento", "fechaReconocimiento",
    "tiempo_reconocimiento", "tiempoReconocimiento",
    "grupo_local", "grupoLocal"
    );

    // POSTERIORES
    @Override
    public ResponseEntity<?> findAllPosteriores() {
        return ResponseEntity.ok(posterioresRepository.findAll());
    }

@Override
public ResponseEntity<?> findPosteriorById(Integer alertaId) {
    try {
        if (alertaId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "El id no puede ser nulo"));
        }

        // 🔹 1. Buscar alertas posteriores por ID
        List<AlertasPosterioresModel> posterioresList = posterioresRepository.findAllByAlertaid(alertaId);

        if (posterioresList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No se encontró ninguna alerta posterior con id " + alertaId));
        }

        // 🔹 2. Obtener los campos visibles configurados
        List<String> camposVisibles = visibleFieldConfigRepository.findAll()
                .stream()
                .filter(VisibleFieldConfigModel::getVisible)
                .map(VisibleFieldConfigModel::getFieldName)
                .collect(Collectors.toList());

        // 🔹 3. Filtrar los campos visibles de cada alerta usando reflexión
        List<Map<String, Object>> alertasFiltradas = posterioresList.stream()
                .map(alerta -> {
                    Map<String, Object> visibleData = new LinkedHashMap<>();

                    for (String campo : camposVisibles) {
                        try {
                            if (campo == null || campo.isBlank()) continue;

                            String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                            String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

                            // Verifica si el método existe
                            Method getter;
                            try {
                                getter = AlertasPosterioresModel.class.getMethod(getterName);
                            } catch (NoSuchMethodException e) {
                                System.err.println("⚠️ No existe el método " + getterName + " para el campo: " + campo);
                                continue;
                            }

                            Object valor = getter.invoke(alerta);
                            visibleData.put(campo, valor != null ? valor : "—");

                        } catch (Exception e) {
                            System.err.println("⚠️ Error accediendo al campo " + campo + ": " + e.getMessage());
                        }
                    }

                    return visibleData;
                })
                .collect(Collectors.toList());

        // 🔹 4. Retornar lista filtrada
        return ResponseEntity.ok(alertasFiltradas);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Error interno al buscar alerta posterior"));
    }
}

}
