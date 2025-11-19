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
        //System.out.println("🔄 Sincronizando columnas...");

        // 1️⃣ Obtener columnas reales desde la BD
        List<String> columnasReales = alertasRepository.obtenerColumnasDeAlertas();
        //System.out.println("📌 Columnas reales en BD: " + columnasReales);

        // 2️⃣ Obtener columnas configuradas en visible_fields
        List<String> camposExistentes = visibleFieldConfigRepository.findAll()
                .stream()
                .map(VisibleFieldConfigModel::getFieldName)
                .toList();

        //System.out.println("📌 Columnas en configuración: " + camposExistentes);

        // 3️⃣ Comparar y agregar las faltantes
        for (String columna : columnasReales) 
        {
            if (!camposExistentes.contains(columna)) 
            {
                VisibleFieldConfigModel nuevoCampo = VisibleFieldConfigModel.builder()
                        .fieldName(columna)
                        .visible(true)
                        .build();

                //System.out.println("➕ Insertando nueva columna visible: " + columna);

                visibleFieldConfigRepository.save(nuevoCampo);
            }
        }

        //System.out.println("✔ Sincronización completada.");
    }


public void sincronizarCamposVisiblesDeAlertasFilterACamposVisibles() {

    // 1️⃣ Obtener columnas reales desde BD
    List<String> columnasReales = alertasRepository.obtenerColumnasDeAlertas();

    // 2️⃣ Obtener columnas ya existentes pero en minúsculas
    List<String> camposExistentes =
            visibleFieldConfigFilterRepository.findAll()
                    .stream()
                    .map(v -> v.getFieldName().toLowerCase())
                    .toList();

    // 3️⃣ Comparar normalizando
    for (String columna : columnasReales) {

        if (!camposExistentes.contains(columna.toLowerCase())) {

            VisibleFieldConfigFilterModel nuevoCampo =
                    VisibleFieldConfigFilterModel.builder()
                            .fieldName(columna) // se guarda tal como viene
                            .visible(true)
                            .build();

            visibleFieldConfigFilterRepository.save(nuevoCampo);
        }
    }
}


}
