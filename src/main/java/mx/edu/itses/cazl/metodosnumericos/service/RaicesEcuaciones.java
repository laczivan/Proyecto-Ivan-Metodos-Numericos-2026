package mx.edu.itses.cazl.metodosnumericos.service;

import mx.edu.itses.cazl.metodosnumericos.dto.request.raices.Biseccion;
import mx.edu.itses.cazl.metodosnumericos.dto.response.raices.BiseccionRespuesta;
import java.util.List;

// Cambiamos "class" por "interface" según el requerimiento 3.6
public interface RaicesEcuaciones {
    
    // Método que recibe el modelo Biseccion y regresa la lista de respuestas
    List<BiseccionRespuesta> biseccion(Biseccion request);
    
}
