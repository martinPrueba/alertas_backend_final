package com.kim21.alertas.service.singularidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Entity
@Table(name = "singularidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingularidadModel 
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "singularidadId")
    private Integer singularidadId;

    @Column(name = "gpsx")
    private Double gpsx;

    @Column(name = "gpsy")
    private Double gpsy;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "activo")
    private String activo;

    @Column(name = "radio")
    private Double radio;

    @Column(name = "evento")
    private String evento;

    @Column(name = "gpsz")
    private Double gpsz;

    @Column(name = "nodo")
    private String nodo;

    @Column(name = "subtipo")
    private String subtipo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fechaidentificacion")
    private OffsetDateTime fechaIdentificacion;
}