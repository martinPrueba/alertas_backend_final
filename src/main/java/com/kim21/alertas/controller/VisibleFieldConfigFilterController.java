package com.kim21.alertas.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kim21.alertas.model.VisibleFieldConfigFilterModel;
import com.kim21.alertas.model.VisibleFieldConfigModel;
import com.kim21.alertas.service.VisibleFieldConfigFilterService;
import com.kim21.alertas.util.AlertasUtils;

@RestController
@RequestMapping("/api/visible-fields-filter")
public class VisibleFieldConfigFilterController 
{
    private final VisibleFieldConfigFilterService service;

    private final AlertasUtils alertasUtils;

    public VisibleFieldConfigFilterController(VisibleFieldConfigFilterService service,AlertasUtils alertasUtils) 
    {
        this.service = service;
        this.alertasUtils = alertasUtils;
    }

    @GetMapping("get-all")
    public ResponseEntity<?> getAll() {
        alertasUtils.sincronizarCamposVisiblesDeAlertasFilterACamposVisibles();
        return service.findAll();
    }
    
    @PutMapping("/update-all")
    public ResponseEntity<?> updateAll(@RequestBody List<VisibleFieldConfigFilterModel> fields) 
    {
        return service.updateAll(fields);
    }

}
