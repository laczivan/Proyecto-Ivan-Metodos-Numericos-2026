package mx.edu.itses.cazl.metodosnumericos.dto.request.sistemaslineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la captura de la dimensión y los coeficientes de la matriz cuadrada.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeterminanteRequest {
    private int dimension;
    private double[][] matriz;
}