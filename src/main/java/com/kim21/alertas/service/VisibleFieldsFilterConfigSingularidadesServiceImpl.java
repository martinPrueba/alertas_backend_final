package com.kim21.alertas.service;

import com.kim21.alertas.model.SingularidadesVisibleFieldFilterModel;
import com.kim21.alertas.repository.SingularidadesVisibleFieldFilterRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class VisibleFieldsFilterConfigSingularidadesServiceImpl implements VisibleFieldsFilterConfigSingularidadesService
{
    @Autowired
    private SingularidadesVisibleFieldFilterRepository repository;

    @Override
    public ResponseEntity<?> getAll()
    {
        return ResponseEntity.ok(repository.findAll());
    }

    @Override
    public ResponseEntity<?> create(SingularidadesVisibleFieldFilterModel body)
    {
        return ResponseEntity.ok(repository.save(body));
    }

    @Override
    public ResponseEntity<?> update(Long id, SingularidadesVisibleFieldFilterModel body)
    {
        // Buscar registro existente
        Optional<SingularidadesVisibleFieldFilterModel> optional =
                repository.findById(id);

        // Validar existencia
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe configuración con ese id");
        }

        // Obtener registro existente
        SingularidadesVisibleFieldFilterModel existing = optional.get();

        // Actualizar campos
        existing.setFieldName(body.getFieldName());
        existing.setVisible(body.getVisible());

        // Guardar cambios
        SingularidadesVisibleFieldFilterModel updated = repository.save(existing);

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
}
