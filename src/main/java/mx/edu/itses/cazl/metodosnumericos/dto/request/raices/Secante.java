package mx.edu.itses.cazl.metodosnumericos.dto.request.raices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la captura de parámetros de entrada del Método de la Secante.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Secante {

    private double xiMenos1;
    private double xi;
    private String fx;
    private double er;
    private int maximoIteraciones;
}