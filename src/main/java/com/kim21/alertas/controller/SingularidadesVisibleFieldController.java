package com.kim21.alertas.controller;

import com.kim21.alertas.model.SingularidadEstadisticasVisibleFieldModel;
import com.kim21.alertas.model.SingularidadesVisibleFieldFilterModel;
import com.kim21.alertas.model.SingularidadesVisibleFieldModel;
import com.kim21.alertas.repository.SingularidadEstadisticasVisibleFieldRepository;
import com.kim21.alertas.repository.SingularidadesVisibleFieldFilterRepository;
import com.kim21.alertas.repository.SingularidadesVisibleFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/singularidades/visible-fields")
public class SingularidadesVisibleFieldController
{
    @Autowired
    private SingularidadesVisibleFieldRepository visibleFieldRepository;

    @Autowired
    private SingularidadesVisibleFieldFilterRepository filterRepository;

    @Autowired
    private SingularidadEstadisticasVisibleFieldRepository estadisticasVisibleFieldRepository;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllVisibleFields()
    {
        try
        {
            Map<String, Boolean> map = new HashMap<>();

            for (SingularidadesVisibleFieldModel visibleField : visibleFieldRepository.findAll())
            {
                if (visibleField.getFieldName() != null)
                {
                    map.put(visibleField.getFieldName(), visibleField.getVisible());
                }
            }

            return ResponseEntity.ok(map);

        }
        catch (Exception e)
        {
            return ResponseEntity.status(500).body(Map.of("error", "Ha ocurrido un error interno."));
        }
    }

    @PutMapping("/update-all")
    public ResponseEntity<?> updateAll(@RequestBody List<SingularidadesVisibleFieldModel> fields)
    {
        try
        {
            for (SingularidadesVisibleFieldModel field : fields)
            {
                visibleFieldRepository.save(field);
            }
            return ResponseEntity.ok(Map.of("message", "Campos actualizados correctamente"));
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error al actualizar campos", "error", e.getMessage()));
        }

    }

    @GetMapping("/filters/get-all")
    public ResponseEntity<?> getAllFiltersVisibleFields()
    {
        try
        {
            Map<String, Boolean> map = new HashMap<>();
            for (SingularidadesVisibleFieldFilterModel filter : filterRepository.findAll())
            {
                if (filter.getFieldName() != null)
                {
                    map.put(filter.getFieldName(), filter.getVisible());
                }
            }
            return ResponseEntity.ok(map);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(500).body(Map.of("error", "Ha ocurrido un error interno."));
        }
    }

    @PutMapping("/filters/update-all")
    public ResponseEntity<?> updateFilters(@RequestBody List<SingularidadesVisibleFieldFilterModel> filters)
    {
        try
        {
            for (SingularidadesVisibleFieldFilterModel filter : filters)
            {
                filterRepository.save(filter);
            }
            return ResponseEntity.ok(Map.of("message", "Campos actualizados correctamente"));
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error al actualizar campos", "error", e.getMessage()));
        }
    }

    @GetMapping("/estadisticas/get-all")
    public ResponseEntity<?> getAllEstadisticasVisibleFields()
    {
        try
        {
            Map<String, Boolean> map = new HashMap<>();
            for (SingularidadEstadisticasVisibleFieldModel visibleField : estadisticasVisibleFieldRepository.findAll())
            {
                if (visibleField.getFieldName() != null)
                {
                    map.put(visibleField.getFieldName(), visibleField.getVisible());
                }
            }
            return ResponseEntity.ok(map);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(500).body(Map.of("error", "Ha ocurrido un error interno."));
        }
    }

    @PutMapping("/estadisticas/update-all")
    public ResponseEntity<?> updateEstadisticas(@RequestBody List<SingularidadEstadisticasVisibleFieldModel> fields)
    {
        try
        {
            for (SingularidadEstadisticasVisibleFieldModel field : fields)
            {
                estadisticasVisibleFieldRepository.save(field);
            }
            return ResponseEntity.ok(Map.of("message", "Campos actualizados correctamente"));
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error al actualizar campos", "error", e.getMessage()));
        }
    }
}
