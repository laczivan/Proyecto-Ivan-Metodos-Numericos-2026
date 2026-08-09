package mx.edu.itses.cazl.metodosnumericos.dto.response.sistemasnolineales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO global que contiene el historial de convergencia y el resultado final del sistema no lineal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewtonRaphsonNoLinealRespuesta {
    private String f1Original;
    private String f2Original;
    private String df1dxStr;
    private String df1dyStr;
    private String df2dxStr;
    private String df2dyStr;
    private double x0;
    private double y0;
    private double solucionX;
    private double solucionY;
    private List<NewtonRaphsonNoLinealIteracion> iteraciones;
    private boolean convergio;
    private List<String> observaciones;
}