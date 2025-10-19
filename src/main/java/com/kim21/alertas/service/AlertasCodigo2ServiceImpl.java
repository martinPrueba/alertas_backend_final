package com.kim21.alertas.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kim21.alertas.model.AlertasCodigo2Model;
import com.kim21.alertas.repository.AlertasCodigo2Repository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertasCodigo2ServiceImpl implements AlertasCodigo2Service 
{

    private final AlertasCodigo2Repository alertasCodigo2Repository;

    @Override
    public List<AlertasCodigo2Model> findAll() {
        return alertasCodigo2Repository.findAll();
    }

    @Override
    public Optional<AlertasCodigo2Model> findById(Integer id) {
        return alertasCodigo2Repository.findById(id);
    }

    @Override
    public AlertasCodigo2Model save(AlertasCodigo2Model model) {
        return alertasCodigo2Repository.save(model);
    }

    @Override
    public void delete(Integer id) {
        alertasCodigo2Repository.deleteById(id);
    }
}