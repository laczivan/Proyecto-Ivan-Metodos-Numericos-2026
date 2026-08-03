package mx.edu.itses.cazl.metodosnumericos.dto.response.raices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReglaFalsaRespuesta {

    private int iteracion;
    private double xl;
    private double xu;
    private double xr;
    private String fx;
    private double fxl;
    private double fxu;
    private double fxr;
    private double er;
}
