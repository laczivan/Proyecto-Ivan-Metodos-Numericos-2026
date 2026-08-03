package mx.edu.itses.cazl.metodosnumericos.dto.response.raices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecanteRespuesta {

    private int iteracion;
    private double xiMenos1;
    private double xi;
    private double xiMas1;
    private double fxiMenos1;
    private double fxi;
    private double fxiMas1;
    private double er;
}