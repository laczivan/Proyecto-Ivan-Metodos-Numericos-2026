package mx.edu.itses.cazl.metodosnumericos.dto.request.derivacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para capturar los parámetros de entrada del usuario
 * para el cálculo de diferenciación numérica.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DerivacionRequest {

    private String funcion;       // Expresión matemática f(x)
    private double x;             // Punto x0 a evaluar
    private double h;             // Tamaño del incremento/paso h
    private int ordenDerivada;    // Orden de la derivada (1, 2, 3 o 4)
}