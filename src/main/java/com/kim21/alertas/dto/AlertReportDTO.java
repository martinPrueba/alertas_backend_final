package com.kim21.alertas.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AlertReportDTO 
{
    private List<Object> alertas;
    private List<Object> alertasLeidas;
}
