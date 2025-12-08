package com.kim21.alertas.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "singularidadesestadisticas", schema = "dbo")
@Data
public class SingularidadesEstadisticasModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "singularidadesestadisticasid")
    private Integer singularidadesestadisticasid;

    @Column(name = "singularidadid")
    private Integer singularidadid;

    @Column(name = "variableid")
    private Integer variableid;

    @Column(name = "tipocurva")
    private String tipocurva;

    @Column(name = "Nmuestras")
    private Integer Nmuestras;

    @Column(name = "SumaVariable")
    private BigDecimal SumaVariable;

    @Column(name = "Sumaxactual")
    private BigDecimal Sumaxactual;

    @Column(name = "PromedioVariable")
    private BigDecimal PromedioVariable;

    @Column(name = "Promedioxactual")
    private BigDecimal Promedioxactual;

    @Column(name = "Sumadesviacionx2")
    private BigDecimal Sumadesviacionx2;

    @Column(name = "Sumadesviaciony2")
    private BigDecimal Sumadesviaciony2;

    @Column(name = "Varianzax")
    private BigDecimal Varianzax;

    @Column(name = "Varianzay")
    private BigDecimal Varianzay;

    @Column(name = "Sumaproductodesviaciones")
    private BigDecimal Sumaproductodesviaciones;

    @Column(name = "b0")
    private BigDecimal b0;

    @Column(name = "b1")
    private BigDecimal b1;

    @Column(name = "Sumaproudctofracionvar")
    private BigDecimal Sumaproudctofracionvar;

    @Column(name = "r")
    private BigDecimal r;

    @Column(name = "r2")
    private BigDecimal r2;
}
