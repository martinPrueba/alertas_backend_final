package com.kim21.alertas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kim21.alertas.model.VisibleFieldConfigModel;
import com.kim21.alertas.repository.VisibleFieldConfigRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visible-fields")
public class VisibleFieldConfigController 
{
    @Autowired
    private VisibleFieldConfigRepository repository;


    @GetMapping("/get-all")
    public ResponseEntity<?> getAllVisibleFields() 
    {
        try 
        {
            Map<String,Boolean> map = new HashMap<>();

            for (VisibleFieldConfigModel visibleField : repository.findAll()) 
            {
                if(!visibleField.getFieldName().equals("alertaid") && !visibleField.getFieldName().equals("gpsx") && !visibleField.getFieldName().equals("gpsy"))
                {
                    if(visibleField.getFieldName() != null)
                    {
                        map.put(visibleField.getFieldName(), visibleField.getVisible());
                    }
                }
            }

            return ResponseEntity.ok(map);

        } 
        catch (Exception e) 
        {
            // TODO: handle exception
            return ResponseEntity.status(500).body(Map.of("error","Ha ocurrido un error interno."));
        }
    }

    @PutMapping("/update-all")
    public ResponseEntity<?> updateAll(@RequestBody List<VisibleFieldConfigModel> fields) 
    {
        try 
        {
            for (VisibleFieldConfigModel field : fields) 
            {
                repository.save(field); // como el @Id es fieldName, hará update automático
            }
            return ResponseEntity.ok(Map.of("message", "Campos actualizados correctamente"));
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error al actualizar campos", "error", e.getMessage()));
        }

    }
}