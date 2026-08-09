package mx.edu.itses.cazl.metodosnumericos.dto.response.sistemasnolineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para almacenar los resultados numéricos obtenidos en cada paso del proceso iterativo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PuntoFijoNoLinealIteracion {
    private int iteracion;
    private double x;
    private double y;
    private double g1Evaluado;
    private double g2Evaluado;
    private double errorX;
    private double errorY;
    private double errorMaximo;
}