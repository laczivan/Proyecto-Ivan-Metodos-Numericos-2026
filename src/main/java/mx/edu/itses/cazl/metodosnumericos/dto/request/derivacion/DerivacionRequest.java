package mx.edu.itses.cazl.metodosnumericos.dto.request.derivacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DerivacionRequest {
    private String funcion;
    private double x;
    private double h;
    private int ordenDerivada;
    
    // Nuevos campos agregados
    private String direccion;
    private String exactitud;
}