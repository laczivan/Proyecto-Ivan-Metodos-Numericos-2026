package mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para la respuesta global del cálculo por el Método de Gauss-Seidel.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GaussSeidelRespuesta {
    private int dimension;
    private double[][] matrizAOriginal;
    private double[] vectorBOriginal;
    private double[] vectorX0;
    private double[] vectorSolucion;
    private List<GaussSeidelIteracion> iteraciones;
    private boolean esDiagonalmenteDominante;
    private boolean convergio;
    private String metodoUtilizado;
    private List<String> observaciones;
}