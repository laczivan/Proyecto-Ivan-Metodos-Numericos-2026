package mx.edu.itses.cazl.metodosnumericos.dto.request.ajustecurvas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiferenciasDivididasRequest {
    private int numeroPuntos; // Restringido entre 2 y 5 (Grado máximo 4 = 5 puntos)
    private List<Double> valoresX;
    private List<Double> valoresY;
    private Double xEvaluar;  // Punto x opcional para evaluar en el polinomio resultante
}