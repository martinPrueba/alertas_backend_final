package com.kim21.alertas.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alertascodigo1")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertasCodigo1Model 
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "codcodigo1", length = 50, nullable = false)
    private String codcodigo1;

    @Column(name = "descripcion", length = 255)
    private String descripcion;
}