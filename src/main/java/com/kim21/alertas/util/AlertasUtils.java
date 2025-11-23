package com.kim21.alertas.util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kim21.alertas.model.AlertasModel;
import com.kim21.alertas.model.VisibleFieldConfigFilterModel;
import com.kim21.alertas.model.VisibleFieldConfigModel;
import com.kim21.alertas.repository.AlertasRepository;
import com.kim21.alertas.repository.VisibleFieldConfigFilterRepository;
import com.kim21.alertas.repository.VisibleFieldConfigRepository;

@Service
public class AlertasUtils 
{

    @Autowired
    private VisibleFieldConfigRepository visibleFieldConfigRepository;

    @Autowired
    private AlertasRepository alertasRepository;

    @Autowired
    private VisibleFieldConfigFilterRepository visibleFieldConfigFilterRepository;

        /**
     * Sincroniza la tabla alerta_visible_fields insertando los campos
     * que existen en AlertasModel pero no están registrados.
     */

public void sincronizarCamposVisiblesDeAlertasACamposVisibles() 
{
    // 1️⃣ Obtener columnas reales normalizadas
    List<String> columnasReales = alertasRepository.obtenerColumnasDeAlertas()
            .stream()
            .map(c -> c.trim().toLowerCase())
            .toList();

    // 2️⃣ Obtener columnas existentes normalizadas
    List<String> camposExistentes = visibleFieldConfigRepository.findAll()
            .stream()
            .map(v -> v.getFieldName().trim().toLowerCase())
            .toList();

    // 3️⃣ Insertar solo las que no existen
    for (String columna : columnasReales) 
    {
        if (!camposExistentes.contains(columna)) 
        {
            try 
            {
                VisibleFieldConfigModel nuevoCampo = VisibleFieldConfigModel.builder()
                        .fieldName(columna) // siempre normalizado
                        .visible(true)
                        .build();

                visibleFieldConfigRepository.save(nuevoCampo);
            } 
            catch (Exception e) 
            {
                System.err.println("⚠ Error insertando campo visible '" + columna + "': " + e.getMessage());
            }
        }
    }
}



public void sincronizarCamposVisiblesDeAlertasFilterACamposVisibles() {

    // 1️⃣ Obtener columnas reales normalizadas
    List<String> columnasReales = alertasRepository.obtenerColumnasDeAlertas()
            .stream()
            .map(c -> c.trim().toLowerCase())
            .toList();

    // 2️⃣ Obtener columnas ya existentes normalizadas
    List<String> camposExistentes = visibleFieldConfigFilterRepository.findAll()
            .stream()
            .map(v -> v.getFieldName().trim().toLowerCase())
            .toList();

    // 3️⃣ Comparar y agregar nuevas
    for (String columna : columnasReales) 
    {
        if (!camposExistentes.contains(columna)) 
        {
            try 
            {
                VisibleFieldConfigFilterModel nuevoCampo =
                        VisibleFieldConfigFilterModel.builder()
                                .fieldName(columna) // guardar siempre normalizado
                                .visible(true)
                                .build();

                visibleFieldConfigFilterRepository.save(nuevoCampo);
            } 
            catch (Exception e) 
            {
                System.err.println("⚠ Error insertando campo dinámico '" + columna + "': " + e.getMessage());
            }
        }
    }
}



}
