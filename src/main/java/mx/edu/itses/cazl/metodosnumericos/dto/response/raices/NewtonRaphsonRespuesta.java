package mx.edu.itses.cazl.metodosnumericos.dto.response.raices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewtonRaphsonRespuesta {
    private int iteracion;
    private double xi;
    private double xi1;
    private double fxi;
    private double fdxi;
    private double er;
}