package mx.edu.itses.cazl.metodosnumericos.dto.request.ajustecurvas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para capturar los datos de entrada requeridos en el método de Regresión Polinomial.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegresionPolinomialRequest {
    private int numeroPuntos;
    private int grado; // Grado del polinomio: 2, 3 o 4
    private List<Double> valoresX;
    private List<Double> valoresY;
}