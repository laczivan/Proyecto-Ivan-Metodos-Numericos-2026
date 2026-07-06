package mx.edu.itses.cazl.metodosnumericos.service;

import mx.edu.itses.cazl.metodosnumericos.dto.request.raices.Biseccion;
import mx.edu.itses.cazl.metodosnumericos.dto.response.raices.BiseccionRespuesta;
import java.util.List;

public interface RaicesService {
    // Método público que regresa un arreglo (o Lista) de objetos BiseccionRespuesta y recibe la clase Biseccion [cite: 174, 175]
    List<BiseccionRespuesta> biseccion(Biseccion request);
}
