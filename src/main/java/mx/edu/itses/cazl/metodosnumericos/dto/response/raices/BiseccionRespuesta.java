package mx.edu.itses.cazl.metodosnumericos.dto.response.raices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiseccionRespuesta {
    // Todos los atributos son double a excepcion de iteracion que es int [cite: 146]
    private int iteracion; // [cite: 137]
    private double xl; // [cite: 138]
    private double xu; // [cite: 139]
    private double xr; // [cite: 140]
    private String fx; // [cite: 141]
    private double fxl; // [cite: 142]
    private double fxu; // [cite: 143]
    private double fxr; // [cite: 144]
    private double er; // [cite: 145]
}