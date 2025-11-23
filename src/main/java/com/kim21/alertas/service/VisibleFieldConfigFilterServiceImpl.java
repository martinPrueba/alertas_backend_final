package com.kim21.alertas.service;

import com.kim21.alertas.model.VisibleFieldConfigFilterModel;
import com.kim21.alertas.repository.AlertasRepository;
import com.kim21.alertas.repository.VisibleFieldConfigFilterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class VisibleFieldConfigFilterServiceImpl implements VisibleFieldConfigFilterService {

    private final VisibleFieldConfigFilterRepository repository;
    private final AlertasRepository alertasRepository;

    public VisibleFieldConfigFilterServiceImpl(VisibleFieldConfigFilterRepository repository,AlertasRepository alertasRepository) 
    {
        this.repository = repository;
        this.alertasRepository = alertasRepository;
    }

    @Override
    public ResponseEntity<?> findAll() 
    {
        List<VisibleFieldConfigFilterModel> list = repository.findAll();

        // 1️⃣ Obtener columnas reales normalizadas
        List<String> columnasReales = alertasRepository.obtenerColumnasDeAlertas()
                .stream()
                .map(c -> c.trim().toLowerCase())
                .toList();
        
        List<VisibleFieldConfigFilterModel> listNew = new ArrayList<>(list);
        list.clear();

        for (VisibleFieldConfigFilterModel visibleFieldConfigFilterModel : listNew) 
        {
            if(columnasReales.contains(visibleFieldConfigFilterModel.getFieldName()))
            {
                list.add(visibleFieldConfigFilterModel);
            }
        }

        
        TreeMap<String, Boolean> map = list.stream()
            .collect(Collectors.toMap(
                VisibleFieldConfigFilterModel::getFieldName,   // key
                VisibleFieldConfigFilterModel::getVisible,     // value
                (oldV, newV) -> newV,                          // fusión si hay duplicados (qué valor gana)
                TreeMap::new                                   // supplier para que sea TreeMap
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