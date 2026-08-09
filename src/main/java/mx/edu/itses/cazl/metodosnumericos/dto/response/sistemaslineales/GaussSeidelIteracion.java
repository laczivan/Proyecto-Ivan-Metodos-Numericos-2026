package mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa el estado del vector solución X y el error en una iteración de Gauss-Seidel.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GaussSeidelIteracion {
    private int iteracion;
    private double[] valoresX;
    private double errorRelativo;
}