package mx.edu.itses.cazl.metodosnumericos.dto.request.integracion;

import lombok.Data;

@Data
public class Simpson13Request {
    private String fx;
    private double a;
    private double b;
    private int n;
}