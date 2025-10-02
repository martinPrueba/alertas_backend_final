package com.kim21.alertas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "visible_field_config_filter")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisibleFieldConfigFilterModel 
{

    @Id
    @Column(name = "field_name")
    private String fieldName; // Ej: "proceso", "criticidad", etc.

    @Column(name = "visible")
    private Boolean visible;  // true o false
}