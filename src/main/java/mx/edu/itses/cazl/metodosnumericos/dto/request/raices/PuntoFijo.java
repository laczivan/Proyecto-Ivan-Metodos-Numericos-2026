package mx.edu.itses.cazl.metodosnumericos.dto.request.raices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuntoFijo {
    private double x0;
    private String gx;
    private double er;
    private int maximoIteraciones;
}