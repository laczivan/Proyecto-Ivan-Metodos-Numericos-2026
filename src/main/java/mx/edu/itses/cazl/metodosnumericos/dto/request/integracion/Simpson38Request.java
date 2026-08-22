package mx.edu.itses.cazl.metodosnumericos.dto.request.integracion;

import lombok.Data;

@Data
public class Simpson38Request {
    private String fx;
    private double a;
    private double b;
}