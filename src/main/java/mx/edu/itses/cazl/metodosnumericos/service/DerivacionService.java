package mx.edu.itses.cazl.metodosnumericos.service;

import mx.edu.itses.cazl.metodosnumericos.dto.request.derivacion.DerivacionRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.response.derivacion.DerivacionRespuesta;

/**
 * Interfaz de servicio para los métodos numéricos de derivación e integración.
 */
public interface DerivacionService {

    /**
     * Realiza la diferenciación numérica de hasta 4to orden calculando
     * aproximaciones hacia adelante, atrás y centradas.
     *
     * @param request Datos de entrada del formulario
     * @return DerivacionRespuesta Resultados detallados
     */
    DerivacionRespuesta calcularDiferenciacion(DerivacionRequest request);
}