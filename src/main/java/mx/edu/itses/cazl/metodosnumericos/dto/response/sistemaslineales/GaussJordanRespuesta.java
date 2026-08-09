package mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para retornar la solución reducida por renglones y la traza detallada del algoritmo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GaussJordanRespuesta {
    private int dimension;
    private double[][] matrizAOriginal;
    private double[] vectorBOriginal;
    private double[][] matrizIdentidad;
    private double[] vectorSolucionX;
    private String metodoUtilizado;
    private List<String> pasosDesarrollo;
}