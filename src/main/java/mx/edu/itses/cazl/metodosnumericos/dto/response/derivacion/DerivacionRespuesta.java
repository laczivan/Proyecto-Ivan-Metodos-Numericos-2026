package mx.edu.itses.cazl.metodosnumericos.dto.response.derivacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Objeto de transferencia de respuesta que agrupa los resultados
 * analíticos y numéricos del método de diferenciación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DerivacionRespuesta {

    private String funcion;
    private double x;
    private double h;
    private int ordenDerivada;
    private String derivadaAnalitica;
    private double valorExacto;
    private List<DerivacionResultadoItem> resultados;
    private List<String> pasosDesarrollo;
}