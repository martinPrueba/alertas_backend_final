package com.kim21.alertas.service;

import com.kim21.alertas.model.VisibleFieldConfigFilterModel;
import com.kim21.alertas.repository.VisibleFieldConfigFilterRepository;
import com.kim21.alertas.service.VisibleFieldConfigFilterService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VisibleFieldConfigFilterServiceImpl implements VisibleFieldConfigFilterService {

    private final VisibleFieldConfigFilterRepository repository;

    public VisibleFieldConfigFilterServiceImpl(VisibleFieldConfigFilterRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResponseEntity<?> findAll() 
    {
        List<VisibleFieldConfigFilterModel> list = repository.findAll();

        // Transformamos la lista en un Map<String, Boolean>
        Map<String, Boolean> map = list.stream()
                .collect(Collectors.toMap(
                        VisibleFieldConfigFilterModel::getFieldName,
                        VisibleFieldConfigFilterModel::getVisible
                ));

        return ResponseEntity.ok(map);
    }


    @Override
    public ResponseEntity<?> updateAll(List<VisibleFieldConfigFilterModel> listVisibleFieldsFilter) 
    {
        try 
        {
            // Recorremos cada uno de los objetos que llegan
            for (VisibleFieldConfigFilterModel item : listVisibleFieldsFilter) 
            {
                repository.findById(item.getFieldName())
                        .map(existing -> {
                            // Si existe -> actualizamos solo el visible
                            existing.setVisible(item.getVisible());
                            return repository.save(existing);
                        })
                        .orElseGet(() -> {
                            // Si no existe -> insertamos nuevo
                            return repository.save(
                                    VisibleFieldConfigFilterModel.builder()
                                            .fieldName(item.getFieldName())
                                            .visible(item.getVisible())
                                            .build()
                            );
                        });
            }

            return ResponseEntity.ok(Map.of("message", "Campos actualizados correctamente"));


        } catch (Exception e) 
        {
            return ResponseEntity.internalServerError().body(Map.of("message","❌ Error al actualizar/insertar VisibleFieldConfigFilter: " + e.getMessage()));
        }

    }


    
}