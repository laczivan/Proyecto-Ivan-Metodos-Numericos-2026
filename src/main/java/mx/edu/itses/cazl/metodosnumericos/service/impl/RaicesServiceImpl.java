package mx.edu.itses.cazl.metodosnumericos.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.itses.cazl.metodosnumericos.dto.request.raices.Biseccion;
import mx.edu.itses.cazl.metodosnumericos.dto.request.raices.ReglaFalsa;
import mx.edu.itses.cazl.metodosnumericos.dto.response.raices.BiseccionRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.raices.ReglaFalsaRespuesta;
import mx.edu.itses.cazl.metodosnumericos.service.RaicesService;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la capa de servicio para los métodos de raíces de ecuaciones[cite: 3].
 * Cumple con principios Clean Code, inmutabilidad y patrones de diseño SOLID.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RaicesServiceImpl implements RaicesService {

    // Inyección por constructor inmutable mediante Lombok @RequiredArgsConstructor
    private final ExprEvaluator exprEvaluator;

    // ==========================================
    // MÉTODO 1: BISECCIÓN (Fase 4)
    // ==========================================
    @Override
    public List<BiseccionRespuesta> biseccion(Biseccion request) {
        log.info("Iniciando algoritmo de Bisección con parámetros: {}", request);
        List<BiseccionRespuesta> resultados = new ArrayList<>();

        double xl = request.getXl();
        double xu = request.getXu();
        double xrAnterior = 0.0;

        // Paso 1 y 2: Criterio inicial de cambio de signo en [XL, XU][cite: 2]
        double fxl = evaluarFuncion(request.getFx(), xl);
        double fxu = evaluarFuncion(request.getFx(), xu);

        if (fxl * fxu >= 0) {
            log.warn("Criterio no cumplido en Bisección: f(xl)*f(xu) = {} >= 0", fxl * fxu);
            throw new IllegalArgumentException("No existe un cambio de signo en el intervalo dado [XL, XU]. Revisa tus valores iniciales.");
        }

        // Paso 0: Ciclo controlado por iteraciones[cite: 2]
        for (int i = 1; i <= request.getMaximoIteraciones(); i++) {
            fxl = evaluarFuncion(request.getFx(), xl);
            fxu = evaluarFuncion(request.getFx(), xu);

            // Paso 3: Cálculo del punto medio[cite: 2]
            double xrActual = (xl + xu) / 2.0;

            // Paso 4: Evaluación de f(xr)[cite: 2]
            double fxr = evaluarFuncion(request.getFx(), xrActual);

            // Paso 6: Cálculo del Error Relativo Porcentual[cite: 2]
            double er = (i == 1) ? 100.0 : Math.abs((xrActual - xrAnterior) / xrActual) * 100.0;

            // Paso 7: Mapeo de DTO de respuesta con patrón Builder[cite: 2]
            BiseccionRespuesta fila = BiseccionRespuesta.builder()
                    .iteracion(i)
                    .xl(xl)
                    .xu(xu)
                    .xr(xrActual)
                    .fx(request.getFx())
                    .fxl(fxl)
                    .fxu(fxu)
                    .fxr(fxr)
                    .er(er)
                    .build();

            resultados.add(fila);

            // Paso 8: Criterio de convergencia por tolerancia[cite: 2]
            if (i > 1 && er < request.getEr()) {
                log.info("Bisección convergió exitosamente en la iteración {} con un error de {}%", i, er);
                break;
            }

            // Paso 5: Selección de subintervalo[cite: 2]
            double producto = fxl * fxr;
            if (producto < 0) {
                xu = xrActual;
            } else if (producto > 0) {
                xl = xrActual;
            } else {
                log.info("Raíz exacta encontrada en Bisección en la iteración {}", i);
                break;
            }

            xrAnterior = xrActual;
        }

        return resultados;
    }

    // ==========================================
    // MÉTODO 2: REGLA FALSA (Fase 5)
    // ==========================================
    @Override
    public List<ReglaFalsaRespuesta> reglaFalsa(ReglaFalsa request) {
        log.info("Iniciando algoritmo de Regla Falsa con parámetros: {}", request);
        List<ReglaFalsaRespuesta> resultados = new ArrayList<>();

        double xl = request.getXl();
        double xu = request.getXu();
        double xrAnterior = 0.0;

        // Paso 1 y 2: Criterio inicial de cambio de signo[cite: 2]
        double fxl = evaluarFuncion(request.getFx(), xl);
        double fxu = evaluarFuncion(request.getFx(), xu);

        if (fxl * fxu >= 0) {
            log.warn("Criterio no cumplido en Regla Falsa: f(xl)*f(xu) = {} >= 0", fxl * fxu);
            throw new IllegalArgumentException("No existe un cambio de signo en el intervalo dado [XL, XU]. Revisa tus valores iniciales.");
        }

        // Paso 0: Ciclo controlado por iteraciones[cite: 2]
        for (int i = 1; i <= request.getMaximoIteraciones(); i++) {
            fxl = evaluarFuncion(request.getFx(), xl);
            fxu = evaluarFuncion(request.getFx(), xu);

            // Paso 3: Interpolación lineal para aproximar la raíz[cite: 2]
            double xrActual = xu - (fxu * (xl - xu)) / (fxl - fxu);

            // Paso 4: Evaluación de f(xr)[cite: 2]
            double fxr = evaluarFuncion(request.getFx(), xrActual);

            // Paso 6: Cálculo del Error Relativo Porcentual[cite: 2]
            double er = (i == 1) ? 100.0 : Math.abs((xrActual - xrAnterior) / xrActual) * 100.0;

            // Paso 7: Mapeo de DTO de respuesta con patrón Builder[cite: 2]
            ReglaFalsaRespuesta fila = ReglaFalsaRespuesta.builder()
                    .iteracion(i)
                    .xl(xl)
                    .xu(xu)
                    .xr(xrActual)
                    .fx(request.getFx())
                    .fxl(fxl)
                    .fxu(fxu)
                    .fxr(fxr)
                    .er(er)
                    .build();

            resultados.add(fila);

            // Paso 8: Criterio de convergencia por tolerancia[cite: 2]
            if (i > 1 && er < request.getEr()) {
                log.info("Regla Falsa convergió exitosamente en la iteración {} con un error de {}%", i, er);
                break;
            }

            // Paso 5: Selección de subintervalo[cite: 2]
            double producto = fxl * fxr;
            if (producto < 0) {
                xu = xrActual;
            } else if (producto > 0) {
                xl = xrActual;
            } else {
                log.info("Raíz exacta encontrada en Regla Falsa en la iteración {}", i);
                break;
            }

            xrAnterior = xrActual;
        }

        return resultados;
    }

    /**
     * Evalúa numéricamente una expresión matemática sustituyendo la variable 'x'.
     * Desacoplado de interfaces específicas de versión para evitar problemas de compilación.
     *
     * @param funcionStr Expresión algebraica en función de 'x'.
     * @param valorX     Valor numérico asignado a la variable 'x'.
     * @return Evaluación numérica en tipo primitivo double.
     */
    private double evaluarFuncion(String funcionStr, double valorX) {
        try {
            // Reemplaza la variable 'x' por el valor y fuerza la evaluación numérica con N()
            String expresion = String.format("N(ReplaceAll(%s, x -> %s))", funcionStr, valorX);
            IExpr result = exprEvaluator.eval(expresion);

            // Parseo directo y universal a tipo primitivo double
            return Double.parseDouble(result.toString());
        } catch (Exception e) {
            log.error("Error al evaluar la función '{}' en x={}: {}", funcionStr, valorX, e.getMessage());
            throw new IllegalArgumentException("Expresión matemática no válida para f(x): " + funcionStr);
        }
    }
}