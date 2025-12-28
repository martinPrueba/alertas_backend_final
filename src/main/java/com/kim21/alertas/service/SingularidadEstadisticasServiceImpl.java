package com.kim21.alertas.service;

import com.kim21.alertas.model.SingularidadEstadisticasVisibleFieldModel;
import com.kim21.alertas.model.SingularidadesEstadisticasModel;
import com.kim21.alertas.repository.SingularidadEstadisticasVisibleFieldRepository;
import com.kim21.alertas.repository.SingularidadesEstadisticasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SingularidadEstadisticasServiceImpl implements SingularidadEstadisticasService
{
    @Autowired
    private SingularidadesEstadisticasRepository singularidadesEstadisticasRepository;

    @Autowired
    private SingularidadEstadisticasVisibleFieldRepository visibleFieldRepository;

    @Override
    public ResponseEntity<?> getAll()
    {
        List<String> camposVisibles = visibleFieldRepository.findAll()
                .stream()
                .filter(SingularidadEstadisticasVisibleFieldModel::getVisible)
                .map(SingularidadEstadisticasVisibleFieldModel::getFieldName)
                .toList();

        List<Map<String, Object>> body = singularidadesEstadisticasRepository.findAll()
                .stream()
                .map(m -> buildVisibleData(camposVisibles, m))
                .toList();

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<?> getById(Integer id)
    {
        Optional<SingularidadesEstadisticasModel> opt = singularidadesEstadisticasRepository.findById(id);
        if (opt.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No existe singularidad estadistica"));
        }

        List<String> camposVisibles = visibleFieldRepository.findAll()
                .stream()
                .filter(SingularidadEstadisticasVisibleFieldModel::getVisible)
                .map(SingularidadEstadisticasVisibleFieldModel::getFieldName)
                .toList();

        return ResponseEntity.ok(buildVisibleData(camposVisibles, opt.get()));
    }

    @Override
    public ResponseEntity<?> getBySingularidadId(Integer singularidadid)
    {
        List<SingularidadesEstadisticasModel> rows = singularidadesEstadisticasRepository.findAllBySingularidadid(singularidadid);
        if (rows.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No existe singularidad estadistica para ese singularidadid"));
        }

        List<String> camposVisibles = visibleFieldRepository.findAll()
                .stream()
                .filter(SingularidadEstadisticasVisibleFieldModel::getVisible)
                .map(SingularidadEstadisticasVisibleFieldModel::getFieldName)
                .toList();

        List<Map<String, Object>> body = rows.stream()
                .map(m -> buildVisibleData(camposVisibles, m))
                .toList();

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<?> create(SingularidadesEstadisticasModel body)
    {
        return ResponseEntity.ok(singularidadesEstadisticasRepository.save(body));
    }

@Override
public ResponseEntity<?> update(Integer id, SingularidadesEstadisticasModel body)
{
    // Buscar registro existente
    Optional<SingularidadesEstadisticasModel> optional = 
            singularidadesEstadisticasRepository.findById(id);

    if (!optional.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "No existe singularidad estadística"));
    }

    // Obtener el existente
    SingularidadesEstadisticasModel existing = optional.get();

    // Mantener el ID original
    body.setSingularidadesestadisticasid(existing.getSingularidadesestadisticasid());

    // Guardar actualización
    SingularidadesEstadisticasModel updated = singularidadesEstadisticasRepository.save(body);

    return ResponseEntity.ok(updated);
}


    @Override
    public ResponseEntity<?> delete(Integer id)
    {
        if (!singularidadesEstadisticasRepository.existsById(id))
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No existe singularidad estadistica"));
        }
        singularidadesEstadisticasRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> buildVisibleData(List<String> camposVisibles, SingularidadesEstadisticasModel model)
    {
        Map<String, Object> visibleData = new HashMap<>();
        for (String campo : camposVisibles)
        {
            try
            {
                String getterName = "get" + Character.toUpperCase(campo.charAt(0)) + campo.substring(1);
                Method getter = SingularidadesEstadisticasModel.class.getMethod(getterName);
                Object valor = getter.invoke(model);
                visibleData.put(campo, valor);
            }
            catch (Exception e)
            {
                System.err.println("Campo ignorado en estadisticas: " + campo + " -> " + e.getMessage());
            }
        }
        return visibleData;
    }
}
