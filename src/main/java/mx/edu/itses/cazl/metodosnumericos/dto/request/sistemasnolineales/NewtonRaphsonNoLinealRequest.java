package mx.edu.itses.cazl.metodosnumericos.dto.request.sistemasnolineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la captura de parámetros del Método de Newton-Raphson
 * para Sistemas de 2 Ecuaciones No Lineales: f1(x,y) = 0 y f2(x,y) = 0.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewtonRaphsonNoLinealRequest {
    private String f1; // Función f1(x, y) = 0
    private String f2; // Función f2(x, y) = 0
    private double x0;
    private double y0;
    private double tolerancia;
    private int maximoIteraciones;
}