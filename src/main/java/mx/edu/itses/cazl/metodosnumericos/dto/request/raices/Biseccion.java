package mx.edu.itses.cazl.metodosnumericos.dto.request.raices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Biseccion {
    // Todos los atributos numéricos son double a excepción de MaximoIteraciones [cite: 134]
    private double xl; // [cite: 129]
    private double xu; // [cite: 130]
    private String fx; // El atributo FX es de tipo String [cite: 131, 135]
    private double er; // [cite: 132]
    // MaximoIteraciones es del tipo int [cite: 134]
    private int maximoIteraciones; // [cite: 133]
}
