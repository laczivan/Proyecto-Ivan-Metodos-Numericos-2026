package mx.edu.itses.cazl.metodosnumericos.service;

import mx.edu.itses.cazl.metodosnumericos.dto.request.integracion.IntegracionMultipleRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.integracion.Simpson13Request;
import mx.edu.itses.cazl.metodosnumericos.dto.request.integracion.Simpson38Request;
import mx.edu.itses.cazl.metodosnumericos.dto.request.integracion.TrapecioRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.response.integracion.IntegracionMultipleRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.integracion.Simpson13Respuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.integracion.Simpson38Respuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.integracion.TrapecioRespuesta;

public interface IntegracionService {
    TrapecioRespuesta calcularTrapecio(TrapecioRequest request);
    Simpson13Respuesta calcularSimpson13(Simpson13Request request);
    Simpson38Respuesta calcularSimpson38(Simpson38Request request);
    IntegracionMultipleRespuesta calcularMultiple(IntegracionMultipleRequest request);
}