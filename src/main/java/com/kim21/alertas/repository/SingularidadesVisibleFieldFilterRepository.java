package com.kim21.alertas.repository;

import com.kim21.alertas.model.SingularidadesVisibleFieldFilterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SingularidadesVisibleFieldFilterRepository extends JpaRepository<SingularidadesVisibleFieldFilterModel, Long>
{
}
