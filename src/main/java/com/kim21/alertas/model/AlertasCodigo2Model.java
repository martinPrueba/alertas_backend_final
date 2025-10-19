package com.kim21.alertas.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alertascodigo2")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertasCodigo2Model 
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "codcodigo2", length = 50, nullable = false)
    private String codcodigo2;

    @Column(name = "descripcion", length = 255)
    private String descripcion;
}
