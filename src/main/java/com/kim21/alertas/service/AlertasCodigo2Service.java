package com.kim21.alertas.service;

import java.util.List;
import java.util.Optional;
import com.kim21.alertas.model.AlertasCodigo2Model;

public interface AlertasCodigo2Service {

    List<AlertasCodigo2Model> findAll();

    Optional<AlertasCodigo2Model> findById(Integer id);

    AlertasCodigo2Model save(AlertasCodigo2Model model);

    void delete(Integer id);
}