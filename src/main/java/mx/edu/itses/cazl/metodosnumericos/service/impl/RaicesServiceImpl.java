package mx.edu.itses.cazl.metodosnumericos.service.impl;

import lombok.extern.slf4j.Slf4j;
import mx.edu.itses.cazl.metodosnumericos.dto.request.raices.Biseccion;
import mx.edu.itses.cazl.metodosnumericos.dto.response.raices.BiseccionRespuesta;
import mx.edu.itses.cazl.metodosnumericos.service.RaicesService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RaicesServiceImpl implements RaicesService {
    
    // Genera el método que implementa la interfaz, recibe el modelo y regresa el arreglo de respuestas [cite: 176, 177]
    @Override
    public List<BiseccionRespuesta> biseccion(Biseccion request) {
        log.info("Iniciando cálculo de Bisección...");
        List<BiseccionRespuesta> resultados = new ArrayList<>();
        
        // TODO: Implementar el algoritmo matemático conectando con Symja en la siguiente fase.
        
        return resultados;
    }
}
