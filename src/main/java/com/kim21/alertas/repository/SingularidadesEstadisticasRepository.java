package com.kim21.alertas.repository;

import com.kim21.alertas.model.SingularidadesEstadisticasModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SingularidadesEstadisticasRepository extends JpaRepository<SingularidadesEstadisticasModel, Integer>
{
    java.util.List<SingularidadesEstadisticasModel> findAllBySingularidadid(Integer singularidadid);
}
