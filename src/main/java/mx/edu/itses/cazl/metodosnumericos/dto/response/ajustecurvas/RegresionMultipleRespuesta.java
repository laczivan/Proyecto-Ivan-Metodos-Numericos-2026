package mx.edu.itses.cazl.metodosnumericos.dto.response.ajustecurvas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta con los resultados estadísticos y el desarrollo paso a paso.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegresionMultipleRespuesta {
    private int numeroPuntos;
    private double a0;
    private double a1;
    private double a2;
    private double sumatoriaX1;
    private double sumatoriaX2;
    private double sumatoriaY;
    private double sumatoriaX1Cuadrado;
    private double sumatoriaX2Cuadrado;
    private double sumatoriaX1X2;
    private double sumatoriaX1Y;
    private double sumatoriaX2Y;
    private double coeficienteDeterminacionR2;
    private double coeficienteCorrelacionR;
    private double error;
    private String ecuacionResultante;
    private List<String> pasosDesarrollo;
}