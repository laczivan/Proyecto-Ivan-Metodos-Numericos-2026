package mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para retornar los resultados del cálculo del determinante y su procedimiento.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeterminanteRespuesta {
    private int dimension;
    private double[][] matrizOriginal;
    private double determinante;
    private String metodoUtilizado;
    private List<String> pasosDesarrollo;
}