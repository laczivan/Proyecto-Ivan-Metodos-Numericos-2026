package mx.edu.itses.cazl.metodosnumericos.dto.request.ajustecurvas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la captura de variables en la Regresión Lineal Múltiple.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegresionMultipleRequest {
    private int numeroPuntos;
    private double[] valoresX1;
    private double[] valoresX2;
    private double[] valoresY;
}