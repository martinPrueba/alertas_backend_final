package com.kim21.alertas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kim21.alertas.model.AlertasCodigo1Model;

@Repository
public interface AlertasCodigo1Repository extends JpaRepository<AlertasCodigo1Model, Integer> {

    // Permite buscar por código si lo necesitas
    AlertasCodigo1Model findByCodcodigo1(String codcodigo1);

    boolean existsByCodcodigo1(String codigo1);
}