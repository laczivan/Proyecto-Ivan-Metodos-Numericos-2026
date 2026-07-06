package mx.edu.itses.cazl.metodosnumericos.service.impl;

import lombok.extern.slf4j.Slf4j;
import mx.edu.itses.cazl.metodosnumericos.dto.request.raices.Biseccion;
import mx.edu.itses.cazl.metodosnumericos.dto.response.raices.BiseccionRespuesta;
import mx.edu.itses.cazl.metodosnumericos.service.RaicesEcuaciones;
import org.matheclipse.core.eval.ExprEvaluator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service 
public class RaicesEcuacionesImplementation implements RaicesEcuaciones {

    @Override
    public List<BiseccionRespuesta> biseccion(Biseccion request) {
        log.info("Iniciando servicio de cálculo de Bisección...");
        
        List<BiseccionRespuesta> iteraciones = new ArrayList<>();
        ExprEvaluator util = new ExprEvaluator(); // Motor matemático Symja
        
        double xl = request.getXl();
        double xu = request.getXu();
        String fx = request.getFx();
        double tolerancia = request.getEr();
        int maxIter = request.getMaximoIteraciones();
        
        double fxl = evaluarFuncion(util, fx, xl);
        double fxu = evaluarFuncion(util, fx, xu);
        
        // Evaluar si existe una raíz en el intervalo
        if (fxl * fxu >= 0) {
            log.warn("No se garantiza una raíz en el intervalo dado.");
            return iteraciones; 
        }

        double xrAnterior = 0;
        double erActual = 100.0; 
        int iteracionActual = 1;

        // Ciclo repetitivo y criterio de convergencia
        while (erActual > tolerancia && iteracionActual <= maxIter) {
            double xrActual = (xl + xu) / 2.0;
            double fxr = evaluarFuncion(util, fx, xrActual);

            if (iteracionActual > 1) {
                erActual = Math.abs((xrActual - xrAnterior) / xrActual) * 100.0;
            }

            // Usamos el Builder de Lombok para crear la respuesta de esta iteración
            BiseccionRespuesta respuesta = BiseccionRespuesta.builder()
                    .iteracion(iteracionActual)
                    .xl(xl)
                    .xu(xu)
                    .xr(xrActual)
                    .fx(fx)
                    .fxl(fxl)
                    .fxu(fxu)
                    .fxr(fxr)
                    .er(erActual)
                    .build();
            iteraciones.add(respuesta);

            // Evaluar subintervalos
            double producto = fxl * fxr;
            if (producto < 0) {
                xu = xrActual;
                fxu = fxr; 
            } else if (producto > 0) {
                xl = xrActual;
                fxl = fxr; 
            } else {
                erActual = 0; // Raíz exacta encontrada
            }

            xrAnterior = xrActual;
            iteracionActual++;
        }

        return iteraciones; 
    }

    // Método auxiliar para evaluar la función
    private double evaluarFuncion(ExprEvaluator util, String funcion, double valorX) {
        String expresionAValidar = funcion.replace("x", String.valueOf(valorX));
        return util.evalf(expresionAValidar);
    }
}