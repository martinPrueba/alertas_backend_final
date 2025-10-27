package com.kim21.alertas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kim21.alertas.model.AlertasPreviosModel;

@Repository
public interface AlertasPreviosRepository extends JpaRepository<AlertasPreviosModel, Integer> {

    List<AlertasPreviosModel> findAllByAlertaid(Integer alertaId);
    
}