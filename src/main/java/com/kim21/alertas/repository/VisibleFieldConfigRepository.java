package com.kim21.alertas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kim21.alertas.model.VisibleFieldConfigModel;

public interface VisibleFieldConfigRepository extends JpaRepository<VisibleFieldConfigModel, String> 
{

    void deleteAllByFieldNameIn(List<String> visibleFieldConfigToDelete);
    
}

