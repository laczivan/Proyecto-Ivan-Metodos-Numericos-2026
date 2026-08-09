package mx.edu.itses.cazl.metodosnumericos.dto.request.sistemaslineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para capturar los parámetros de entrada del Método Iterativo de Gauss-Seidel.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GaussSeidelRequest {
    private int dimension;
    private double[][] matrizA;
    private double[] vectorB;
    private double[] vectorX0;
    private double tolerancia;
    private int maximoIteraciones;
}