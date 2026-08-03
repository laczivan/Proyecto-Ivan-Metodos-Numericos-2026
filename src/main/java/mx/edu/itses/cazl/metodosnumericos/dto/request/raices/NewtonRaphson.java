package mx.edu.itses.cazl.metodosnumericos.dto.request.raices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewtonRaphson {
    private double xi;
    private String fx;
    private double er;
    private int maximoIteraciones;
}