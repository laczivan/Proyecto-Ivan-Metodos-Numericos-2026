package mx.edu.itses.cazl.metodosnumericos.dto.request.integracion;

import lombok.Data;

@Data
public class IntegracionMultipleRequest {
    private String fxy;
    private double ax;
    private double bx;
    private double ay;
    private double by;
    private int nx;
    private int ny;
}