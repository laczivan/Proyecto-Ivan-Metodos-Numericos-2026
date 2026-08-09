package mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para encapsular la respuesta global de la solución por el Método de Jacobi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JacobiRespuesta {
    private int dimension;
    private double[][] matrizAOriginal;
    private double[] vectorBOriginal;
    private double[] vectorX0;
    private double[] vectorSolucionX;
    private List<JacobiIteracionRespuesta> iteraciones;
    private boolean esDiagonalmenteDominante;
    private boolean convergio;
    private String metodoUtilizado;
    private List<String> observaciones;
}