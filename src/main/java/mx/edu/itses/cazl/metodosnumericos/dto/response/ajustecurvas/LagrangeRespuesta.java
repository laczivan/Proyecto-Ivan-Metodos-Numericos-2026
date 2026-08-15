package mx.edu.itses.cazl.metodosnumericos.dto.response.ajustecurvas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para retornar los resultados del Polinomio de Lagrange.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LagrangeRespuesta {

    private int numeroPuntos;
    private int gradoPolinomio;
    private List<Double> valoresX;
    private List<Double> valoresY;
    private List<String> polinomiosBaseL; // Expresiones de los L_i(x)
    private String polinomioResultante;  // Polinomio P(x) simplificado
    private Double xEvaluar;
    private Double yEvaluado;
    private List<String> pasosDesarrollo;
}