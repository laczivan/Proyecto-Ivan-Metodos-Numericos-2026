package mx.edu.itses.cazl.metodosnumericos.dto.response.integracion;

import lombok.Data;
import java.util.List;

@Data
public class IntegracionMultipleRespuesta {
    private String fxy;
    private double ax, bx, ay, by;
    private int nx, ny;
    private double hx, hy;
    private double integralAproximada;
    private double integralExacta;
    private double errorRelativo;
    private List<String> pasosDesarrollo;
}