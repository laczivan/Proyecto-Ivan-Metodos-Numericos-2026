package mx.edu.itses.cazl.metodosnumericos.dto.response.ajustecurvas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO con la estructura de resultados para el ajuste por Regresión Polinomial.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegresionPolinomialRespuesta {
    private int grado;
    private double[] coeficientes; // a0, a1, a2, ..., am
    private double[] sumatoriasX;   // Sumatorias de potencias de X (hasta 2m)
    private double[] sumatoriasXY;  // Sumatorias de productos X^k * Y (hasta m)
    private double[][] matrizSistema; // Matriz aumentada del sistema de ecuaciones normales
    private double coeficienteDeterminacionR2;
    private double coeficienteCorrelacionR;
    private double errorEstandar;  // Sy/x
    private String ecuacionResultante;
    private List<String> pasosDesarrollo;
}