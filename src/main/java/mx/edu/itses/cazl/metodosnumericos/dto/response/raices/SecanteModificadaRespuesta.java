package mx.edu.itses.cazl.metodosnumericos.dto.response.raices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para almacenar los resultados tabulares por iteración del Método de la Secante Modificado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecanteModificadaRespuesta {

    private int iteracion;
    private double xi;
    private double xiMasS;
    private double xiMas1;
    private double fxi;
    private double fxiMasS;
    private double er;
}