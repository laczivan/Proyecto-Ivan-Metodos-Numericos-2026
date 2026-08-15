package mx.edu.itses.cazl.metodosnumericos.dto.response.ajustecurvas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para transferir los coeficientes, sumatorias y trazabilidad de la Regresión Lineal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegresionLinealRespuesta {
    private Integer numeroPuntos;
    private Double sumatoriaX;
    private Double sumatoriaY;
    private Double sumatoriaX2;
    private Double sumatoriaXY;
    private Double promedioX;
    private Double promedioY;
    private Double a0;
    private Double a1;
    private Double coeficienteCorrelacionR;
    private Double coeficienteDeterminacionR2;
    private String ecuacionResultante;
    private List<String> pasosDesarrollo;
}