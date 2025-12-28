package com.kim21.alertas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rango_color_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RangoColorConfigModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String color;

    @Column(name = "min_valor")
    private Double minValor;

    @Column(name = "max_valor")
    private Double maxValor;

    @Column(nullable = false)
    private Integer prioridad;
}
