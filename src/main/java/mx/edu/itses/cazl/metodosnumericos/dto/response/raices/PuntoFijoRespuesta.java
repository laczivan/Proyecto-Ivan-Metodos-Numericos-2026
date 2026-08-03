package mx.edu.itses.cazl.metodosnumericos.dto.response.raices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuntoFijoRespuesta {
    private int iteracion;
    private double x0;
    private double gx0;
    private double er;
}