package mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa los resultados de una iteración individual en el Método de Jacobi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JacobiIteracionRespuesta {
    private int iteracion;
    private double[] vectorX;
    private double errorRelativo;
}