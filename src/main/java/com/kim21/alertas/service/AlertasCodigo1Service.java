package com.kim21.alertas.service;

import java.util.List;
import java.util.Optional;
import com.kim21.alertas.model.AlertasCodigo1Model;

public interface AlertasCodigo1Service 
{
    List<AlertasCodigo1Model> findAll();

    Optional<AlertasCodigo1Model> findById(Integer id);

    AlertasCodigo1Model save(AlertasCodigo1Model model);

    void delete(Integer id);
}