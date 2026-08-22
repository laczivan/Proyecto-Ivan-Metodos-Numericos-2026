package mx.edu.itses.cazl.metodosnumericos.dto.response.integracion;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class Simpson13Respuesta {
    private String fx;
    private double a;
    private double b;
    private int n;
    private double h;
    private double integralAproximada;
    private double integralExacta;
    private double errorRelativo;
    private List<Map<String, Double>> tablaPuntos;
    private List<String> pasosDesarrollo;
}