package mx.edu.itses.cazl.metodosnumericos.dto.request.raices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la captura de parámetros de entrada del Método de la Secante Modificado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecanteModificada {

    private double xi;
    private double sigma; // Perturbación (delta o s)
    private String fx;
    private double er;
    private int maximoIteraciones;
}