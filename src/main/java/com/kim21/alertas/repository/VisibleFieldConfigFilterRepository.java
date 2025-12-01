package com.kim21.alertas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kim21.alertas.model.VisibleFieldConfigFilterModel;

public interface VisibleFieldConfigFilterRepository extends JpaRepository<VisibleFieldConfigFilterModel, String> {

    void deleteAllByFieldNameIn(List<String> visibleFieldConfigToDelete);
}