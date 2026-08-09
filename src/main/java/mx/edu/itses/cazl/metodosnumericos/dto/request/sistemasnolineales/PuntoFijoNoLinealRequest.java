package mx.edu.itses.cazl.metodosnumericos.dto.request.sistemasnolineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la captura de parámetros de entrada en el Método de Iteración de Punto Fijo
 * para Sistemas de Ecuaciones No Lineales de 2 variables.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PuntoFijoNoLinealRequest {
    private String g1; // Expresión x = g1(x, y)
    private String g2; // Expresión y = g2(x, y)
    private double x0;
    private double y0;
    private double tolerancia;
    private int maximoIteraciones;
}