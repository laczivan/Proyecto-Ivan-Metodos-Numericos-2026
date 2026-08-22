package mx.edu.itses.cazl.metodosnumericos.dto.response.integracion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrapecioRespuesta {
    private String fx;
    private double a;
    private double b;
    private int n;
    private double h;
    private double integralAproximada;
    private double integralExacta;
    private List<PuntoEvaluado> tablaPuntos;
    private double errorRelativo;
    private List<String> pasosDesarrollo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PuntoEvaluado {
        private int i;
        private double xi;
        private double fxi;
    }
}