package com.kim21.alertas.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "singularidadestadisticas_visible_field", schema = "dbo")
@Data
public class SingularidadEstadisticasVisibleFieldModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fieldName;

    private Boolean visible;
}
