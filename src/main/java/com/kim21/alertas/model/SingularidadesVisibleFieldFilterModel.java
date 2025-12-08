package com.kim21.alertas.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "singularidades_visible_field_config_filter", schema = "dbo")
@Data
public class SingularidadesVisibleFieldFilterModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fieldName;

    private Boolean visible;
}
