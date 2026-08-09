package mx.edu.itses.cazl.metodosnumericos.dto.response.sistemasnolineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa el estado numérico del sistema en cada iteración k del algoritmo Newton-Raphson 2D.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewtonRaphsonNoLinealIteracion {
    private int iteracion;
    private double x;
    private double y;
    private double f1Evaluado;
    private double f2Evaluado;
    private double jacobiano;
    private double errorX;
    private double errorY;
    private double errorMaximo;
}