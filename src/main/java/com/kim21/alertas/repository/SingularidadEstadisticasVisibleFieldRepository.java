package com.kim21.alertas.repository;

import com.kim21.alertas.model.SingularidadEstadisticasVisibleFieldModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SingularidadEstadisticasVisibleFieldRepository extends JpaRepository<SingularidadEstadisticasVisibleFieldModel, Long>
{
}
