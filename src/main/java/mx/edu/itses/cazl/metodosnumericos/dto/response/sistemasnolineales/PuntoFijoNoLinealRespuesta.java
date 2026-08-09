package mx.edu.itses.cazl.metodosnumericos.dto.response.sistemasnolineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO global de respuesta con la traza completa de convergencia y la solución aproximada.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PuntoFijoNoLinealRespuesta {
    private String g1Original;
    private String g2Original;
    private double x0;
    private double y0;
    private double solucionX;
    private double solucionY;
    private List<PuntoFijoNoLinealIteracion> iteraciones;
    private boolean convergio;
    private List<String> observaciones;
}