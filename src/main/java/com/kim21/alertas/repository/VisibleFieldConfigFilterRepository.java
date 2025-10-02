package com.kim21.alertas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kim21.alertas.model.VisibleFieldConfigFilterModel;

public interface VisibleFieldConfigFilterRepository extends JpaRepository<VisibleFieldConfigFilterModel, String> {
}