package mx.edu.itses.cazl.metodosnumericos.dto.response.derivacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa el resultado de la aproximación para un esquema específico
 * (Adelante, Atrás o Centrada).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DerivacionResultadoItem {

    private String esquema;         // p. ej. "Diferencias Hacia Adelante O(h)"
    private String formulaUsada;    // Fórmula matemática empleada
    private double valorAproximado; // Valor numérico calculado
    private double errorRelativo;   // Error relativo porcentual (%)
}