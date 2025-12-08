package com.kim21.alertas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "singularidades", schema = "dbo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingularidadModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "singularidadId")
    private Integer singularidadId;

    @Column(name = "nombreActivo")
    private String nombreActivo;

    @Column(name = "proceso")
    private String proceso;

    @Column(name = "tipoServicio")
    private String tipoServicio;

    @Column(name = "Subtiposervicio")
    private String subtiposervicio;

    @Column(name = "grupolocal")
    private String grupoLocal;

    @Column(name = "var_independiente")
    private String varIndependiente;

    @Column(name = "var_dependiente")
    private String varDependiente;

    @Column(name = "color")
    private String color;

    @Column(name = "gpsx")
    private Double gpsx;

    @Column(name = "gpsy")
    private Double gpsy;

    @Column(name = "gpsz")
    private Double gpsz;

    @Column(name = "gpsh")
    private Double gpsh;

    @Column(name = "radio")
    private String radio;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "evento")
    private String evento;

    @Column(name = "nodo")
    private String nodo;

    @Column(name = "subtipo")
    private String subtipo;

    @Column(name = "fechasingularidad")
    private OffsetDateTime fechaSingularidad;

    @Column(name = "valida")
    private Boolean valida;

    @Lob
    @Column(name = "comentario")
    private String comentario;

    @Column(name = "OT")
    private String OT;

    @Column(name = "ticket")
    private String ticket;

    @Column(name = "fecha_reconocimiento")
    private OffsetDateTime fechaReconocimiento;

    @Column(name = "userid")
    private String userId;

    @Column(name = "tiempo_reconocimiento")
    private Long tiempoReconocimiento;

    @Column(name = "codigo1")
    private String codigo1;

    @Column(name = "codigo2")
    private String codigo2;

    @Column(name = "userid_alerta")
    private String userIdAlerta;

    @Column(name = "fecha_alerta")
    private OffsetDateTime fechaAlerta;

    @Column(name = "singularidad_alertaid")
    private Integer singularidadAlertaId;

    @Transient
    private String iconAssocieteFromProceso;
}
