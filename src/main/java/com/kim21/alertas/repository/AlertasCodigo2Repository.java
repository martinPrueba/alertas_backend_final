package com.kim21.alertas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kim21.alertas.model.AlertasCodigo2Model;

@Repository
public interface AlertasCodigo2Repository extends JpaRepository<AlertasCodigo2Model, Integer> {

    // Permite buscar por código si se necesita
    AlertasCodigo2Model findByCodcodigo2(String codcodigo2);

    boolean existsByCodcodigo2(String codigo2);
}