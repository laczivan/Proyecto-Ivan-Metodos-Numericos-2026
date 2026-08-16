package mx.edu.itses.cazl.metodosnumericos.service;

import mx.edu.itses.cazl.metodosnumericos.dto.request.ajustecurvas.DiferenciasDivididasRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.ajustecurvas.LagrangeRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.ajustecurvas.RegresionLinealRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.ajustecurvas.RegresionMultipleRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.ajustecurvas.RegresionPolinomialRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.response.ajustecurvas.DiferenciasDivididasRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.ajustecurvas.LagrangeRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.ajustecurvas.RegresionLinealRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.ajustecurvas.RegresionMultipleRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.ajustecurvas.RegresionPolinomialRespuesta;

public interface AjusteCurvasService {
    DiferenciasDivididasRespuesta diferenciasDivididas(DiferenciasDivididasRequest request);
    LagrangeRespuesta lagrange(LagrangeRequest request);
    RegresionLinealRespuesta regresionLineal(RegresionLinealRequest request);
    RegresionPolinomialRespuesta regresionPolinomial(RegresionPolinomialRequest request);
    RegresionMultipleRespuesta regresionMultiple(RegresionMultipleRequest request);
}