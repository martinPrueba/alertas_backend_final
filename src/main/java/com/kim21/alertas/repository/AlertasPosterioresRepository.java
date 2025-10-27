package com.kim21.alertas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kim21.alertas.model.AlertasPosterioresModel;

@Repository
public interface AlertasPosterioresRepository extends JpaRepository<AlertasPosterioresModel, Integer> {

    List<AlertasPosterioresModel> findAllByAlertaid(Integer alertaId);
    
}