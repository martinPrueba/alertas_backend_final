package com.kim21.alertas.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kim21.alertas.model.AlertasModel;
import com.kim21.alertas.model.AlertasPreviosModel;
import com.kim21.alertas.model.ProcessAssociateIconModel;
import com.kim21.alertas.model.VisibleFieldConfigModel;
import com.kim21.alertas.repository.AlertasPreviosRepository;
import com.kim21.alertas.repository.ProcessAssociateIconRepository;
import com.kim21.alertas.repository.VisibleFieldConfigRepository;

@Service
public class AlertasPreviosServiceImpl implements AlertasPreviosService
{
    @Autowired
    private AlertasPreviosRepository previosRepository;

    @Autowired
    private VisibleFieldConfigRepository visibleFieldConfigRepository;

    private static final Map<String, String> COLUMN_TO_FIELD = Map.of(
    "fecha_reconocimiento", "fechaReconocimiento",
    "tiempo_reconocimiento", "tiempoReconocimiento",
    "grupo_local", "grupoLocal"
    );


    @Autowired
    private ProcessAssociateIconRepository processAssociateIconRepository;

    // PREVIOS
    @Override
    public ResponseEntity<?> findAllPrevios() {
        return ResponseEntity.ok(previosRepository.findAll());
    }

@Override
public ResponseEntity<?> findPrevioById(Integer alertaId) {
    try {
        if (alertaId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "El id no puede ser nulo"));
        }

        // 🔹 1. Buscar alertas por ID
        List<AlertasPreviosModel> alertasPreviasList = previosRepository.findAllByAlertaid(alertaId);

        if (alertasPreviasList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No se encontró ninguna alerta previa con id " + alertaId));
        }

        // 🔹 2. Obtener campos visibles configurados
        List<String> camposVisibles = visibleFieldConfigRepository.findAll()
                .stream()
                .filter(VisibleFieldConfigModel::getVisible) // Solo los que están marcados como visibles
                .map(VisibleFieldConfigModel::getFieldName)
                .collect(Collectors.toList());

        // 🔹 3. Filtrar los campos visibles de cada alerta usando reflexión
        List<Map<String, Object>> alertasFiltradas = alertasPreviasList.stream()
                .map(alerta -> {
                    Map<String, Object> visibleData = new LinkedHashMap<>();

                    for (String campo : camposVisibles) {
                        try {
                            if (campo == null || campo.isBlank()) continue;

                            String fieldName = COLUMN_TO_FIELD.getOrDefault(campo, campo);
                            String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

                            // Verifica si el método existe antes de intentar invocarlo
                            Method getter;
                            try {
                                getter = AlertasPreviosModel.class.getMethod(getterName);
                            } catch (NoSuchMethodException e) {
                                System.err.println("⚠️ No existe el método " + getterName + " para el campo: " + campo);
                                continue;
                            }

                            Object valor = getter.invoke(alerta);
                            visibleData.put(campo, valor != null ? valor : "—");

                        } catch (Exception e) {
                            System.err.println("⚠️ Error al acceder al campo " + campo + ": " + e.getMessage());
                        }
                    }

                    return visibleData;
                })
                .collect(Collectors.toList());

        // 🔹 4. Retornar la lista filtrada
        return ResponseEntity.ok(alertasFiltradas);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Error interno al buscar alerta previa"));
    }
}

}
