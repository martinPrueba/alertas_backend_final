package com.kim21.alertas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kim21.alertas.model.AlertasCodigo1Model;
import com.kim21.alertas.repository.AlertasCodigo1Repository;
import com.kim21.alertas.service.AlertasCodigo1Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertasCodigo1ServiceImpl implements AlertasCodigo1Service 
{

    private final AlertasCodigo1Repository alertasCodigo1Repository;

    @Override
    public List<AlertasCodigo1Model> findAll() {
        return alertasCodigo1Repository.findAll();
    }

    @Override
    public Optional<AlertasCodigo1Model> findById(Integer id) {
        return alertasCodigo1Repository.findById(id);
    }

    @Override
    public AlertasCodigo1Model save(AlertasCodigo1Model model) {
        return alertasCodigo1Repository.save(model);
    }

    @Override
    public void delete(Integer id) {
        alertasCodigo1Repository.deleteById(id);
    }
}