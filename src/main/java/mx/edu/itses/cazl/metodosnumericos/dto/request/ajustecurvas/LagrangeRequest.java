package mx.edu.itses.cazl.metodosnumericos.dto.request.ajustecurvas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para capturar los datos de entrada en la Interpolación de Lagrange.
 * Restringido a un grado máximo de orden 4 (hasta 5 puntos de datos).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LagrangeRequest {

    private Integer numeroPuntos; // Entre 2 y 5 puntos (orden 1 a 4)
    private List<Double> valoresX;
    private List<Double> valoresY;
    private Double xEvaluar;      // Punto X objetivo para evaluar el polinomio resultante
}