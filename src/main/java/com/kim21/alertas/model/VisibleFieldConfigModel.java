package com.kim21.alertas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alerta_visible_fields", schema = "dbo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisibleFieldConfigModel 
{
    @Id
    private String fieldName; // Ej: "proceso", "criticidad", etc.

    private Boolean visible; // true o false
}