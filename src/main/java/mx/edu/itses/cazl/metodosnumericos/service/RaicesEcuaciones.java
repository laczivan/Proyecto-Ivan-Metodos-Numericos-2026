package mx.edu.itses.cazl.metodosnumericos.service;

import mx.edu.itses.cazl.metodosnumericos.dto.request.raices.Biseccion;
import mx.edu.itses.cazl.metodosnumericos.dto.request.raices.NewtonRaphson;
import mx.edu.itses.cazl.metodosnumericos.dto.request.raices.PuntoFijo;
import mx.edu.itses.cazl.metodosnumericos.dto.request.raices.ReglaFalsa;
import mx.edu.itses.cazl.metodosnumericos.dto.request.raices.Secante;
import mx.edu.itses.cazl.metodosnumericos.dto.request.raices.SecanteModificada;
import mx.edu.itses.cazl.metodosnumericos.dto.response.raices.BiseccionRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.raices.NewtonRaphsonRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.raices.PuntoFijoRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.raices.ReglaFalsaRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.raices.SecanteModificadaRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.raices.SecanteRespuesta;

import java.util.List;

/**
 * Contrato de la capa de servicio para los métodos numéricos de Raíces de Ecuaciones.
 */
public interface RaicesEcuaciones {

    List<BiseccionRespuesta> biseccion(Biseccion request);

    List<ReglaFalsaRespuesta> reglaFalsa(ReglaFalsa request);
    List<PuntoFijoRespuesta> puntoFijo(PuntoFijo request);
    List<NewtonRaphsonRespuesta> newtonRaphson(NewtonRaphson request);
    List<SecanteRespuesta> secante(Secante request);
    List<SecanteModificadaRespuesta> secanteModificada(SecanteModificada request);
}