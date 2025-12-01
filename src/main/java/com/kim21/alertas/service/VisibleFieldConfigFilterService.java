package com.kim21.alertas.service;

import com.kim21.alertas.model.VisibleFieldConfigFilterModel;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface VisibleFieldConfigFilterService 
{
    ResponseEntity<?> findAll();
    ResponseEntity<?> updateAll(List<VisibleFieldConfigFilterModel> listVisibleFieldsFilter);
    void deleteVisibleFieldConfigColumns();
}