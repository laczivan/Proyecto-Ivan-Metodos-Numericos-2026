package mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para la transferencia de resultados de la Eliminación Gaussiana y Sustitución Hacia Atrás.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EliminacionGaussianaRespuesta {
    private int dimension;
    private double[][] matrizAOriginal;
    private double[] vectorBOriginal;
    private double[][] matrizATriangular;
    private double[] vectorBTriangular;
    private double[] vectorSolucionX;
    private String metodoUtilizado;
    private List<String> pasosDesarrollo;
}