package com.kim21.alertas.service;

import com.kim21.alertas.model.SingularidadEstadisticasVisibleFieldModel;
import com.kim21.alertas.repository.SingularidadEstadisticasVisibleFieldRepository;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class VisibleFieldsConfigSingularidadesEstadisticasServiceImpl implements VisibleFieldsConfigSingularidadesEstadisticasService
{
    @Autowired
    private SingularidadEstadisticasVisibleFieldRepository repository;

    @Override
    public ResponseEntity<?> getAll()
    {
        return ResponseEntity.ok(repository.findAll());
    }

    @Override
    public ResponseEntity<?> create(SingularidadEstadisticasVisibleFieldModel body)
    {
        return ResponseEntity.ok(repository.save(body));
    }

    @Override
    public ResponseEntity<?> update(Long id, SingularidadEstadisticasVisibleFieldModel body)
    {
        // Buscar registro existente
        Optional<SingularidadEstadisticasVisibleFieldModel> optional =
                repository.findById(id);

        // Validar existencia
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe configuración con ese id");
        }

        // Recuperar entidad existente
        SingularidadEstadisticasVisibleFieldModel existing = optional.get();

        // Actualizar campos
        existing.setFieldName(body.getFieldName());
        existing.setVisible(body.getVisible());

        // Guardar cambios
        SingularidadEstadisticasVisibleFieldModel updated = repository.save(existing);

        return ResponseEntity.ok(updated);
    }


    @Override
    public ResponseEntity<?> delete(Long id)
    {
        if (!repository.existsById(id))
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe configuracion con ese id");
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @Override
    @Transactional
    public void updateAllByFieldName(List<SingularidadEstadisticasVisibleFieldModel> lista) {
        for (SingularidadEstadisticasVisibleFieldModel item : lista) {

            SingularidadEstadisticasVisibleFieldModel existing =
                    repository.findByFieldName(item.getFieldName());

            if (existing != null) {
                // actualizar
                existing.setVisible(item.getVisible());
                repository.save(existing);
            } else {
                // opcional: crear si no existe
                SingularidadEstadisticasVisibleFieldModel nuevo = new SingularidadEstadisticasVisibleFieldModel();
                nuevo.setFieldName(item.getFieldName());
                nuevo.setVisible(item.getVisible());
                repository.save(nuevo);
            }
        }
    }
    
}
