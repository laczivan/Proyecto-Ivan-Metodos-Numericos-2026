package mx.edu.itses.cazl.metodosnumericos.dto.request.ajustecurvas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para la captura de los puntos experimentales de la Regresión Lineal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegresionLinealRequest {
    private Integer numeroPuntos;
    private List<Double> valoresX;
    private List<Double> valoresY;
}