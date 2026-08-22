package mx.edu.itses.cazl.metodosnumericos.dto.request.integracion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrapecioRequest {
    private String fx;
    private double a;
    private double b;
    private int n;
}