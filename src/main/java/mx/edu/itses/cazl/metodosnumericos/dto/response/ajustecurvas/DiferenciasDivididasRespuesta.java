package mx.edu.itses.cazl.metodosnumericos.dto.response.ajustecurvas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiferenciasDivididasRespuesta {
    private int numeroPuntos;
    private int gradoPolinomio;
    private List<Double> valoresX;
    private List<Double> valoresY;
    private double[][] tablaDiferencias; // Matriz NxN con la tabla de diferencias divididas
    private List<Double> coeficientesB;   // Coeficientes b0, b1, b2, b3, b4
    private String polinomioResultante;
    private Double xEvaluar;
    private Double resultadoEvaluacion;
    private List<String> pasosDesarrollo;
}