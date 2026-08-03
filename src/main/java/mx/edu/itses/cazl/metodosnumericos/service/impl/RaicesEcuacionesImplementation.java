package mx.edu.itses.cazl.metodosnumericos.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import mx.edu.itses.cazl.metodosnumericos.service.RaicesEcuaciones;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Implementación de la capa de servicio para los métodos numéricos de raíces de ecuaciones[cite: 3].
 * Sigue los principios SOLID, inmutabilidad y evaluación matemática desacoplada del Locale.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RaicesEcuacionesImplementation implements RaicesEcuaciones {

    private final ExprEvaluator exprEvaluator;

    @Override
    public List<BiseccionRespuesta> biseccion(Biseccion request) {
        log.info("Procesando algoritmo de Bisección para: {}", request.getFx());
        List<BiseccionRespuesta> resultados = new ArrayList<>();

        double xl = request.getXl();
        double xu = request.getXu();
        double xrAnterior = 0.0;

        double fxl = evaluarFuncion(request.getFx(), xl);
        double fxu = evaluarFuncion(request.getFx(), xu);

        if (fxl * fxu >= 0) {
            log.warn("Criterio no cumplido en Bisección: f(xl)*f(xu) = {} >= 0", fxl * fxu);
            throw new IllegalArgumentException("No existe un cambio de signo en el intervalo dado [XL, XU]. Revisa tus valores iniciales.");
        }

        for (int i = 1; i <= request.getMaximoIteraciones(); i++) {
            fxl = evaluarFuncion(request.getFx(), xl);
            fxu = evaluarFuncion(request.getFx(), xu);

            double xrActual = (xl + xu) / 2.0;
            double fxr = evaluarFuncion(request.getFx(), xrActual);

            double er = (i == 1) ? 100.0 : Math.abs((xrActual - xrAnterior) / xrActual) * 100.0;

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

            if (i > 1 && er < request.getEr()) {
                log.info("Bisección convergió exitosamente en la iteración {} con un error de {}%", i, er);
                break;
            }

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

    @Override
    public List<ReglaFalsaRespuesta> reglaFalsa(ReglaFalsa request) {
        log.info("Procesando algoritmo de Regla Falsa para: {}", request.getFx());
        List<ReglaFalsaRespuesta> resultados = new ArrayList<>();

        double xl = request.getXl();
        double xu = request.getXu();
        double xrAnterior = 0.0;

        double fxl = evaluarFuncion(request.getFx(), xl);
        double fxu = evaluarFuncion(request.getFx(), xu);

        // Validación del Teorema de Bolzano
        if (fxl * fxu >= 0) {
            log.warn("Criterio no cumplido en Regla Falsa: f(xl)*f(xu) = {} >= 0", fxl * fxu);
            throw new IllegalArgumentException("No existe un cambio de signo en el intervalo dado [XL, XU]. Revisa tus valores iniciales.");
        }

        for (int i = 1; i <= request.getMaximoIteraciones(); i++) {
            fxl = evaluarFuncion(request.getFx(), xl);
            fxu = evaluarFuncion(request.getFx(), xu);

            // Interpolación lineal
            double xrActual = xu - (fxu * (xl - xu)) / (fxl - fxu);
            double fxr = evaluarFuncion(request.getFx(), xrActual);

            double er = (i == 1) ? 100.0 : Math.abs((xrActual - xrAnterior) / xrActual) * 100.0;

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

            if (i > 1 && er < request.getEr()) {
                log.info("Regla Falsa convergió exitosamente en la iteración {} con un error de {}%", i, er);
                break;
            }

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
     * Evalúa expresiones algebraicas sustituyendo la variable 'x'.
     * Normaliza los logaritmos y fuerza Locale.US para garantizar puntos decimales.
     */
    private double evaluarFuncion(String expresionStr, double valorX) {
        // 1. Validación Fail-Fast
        if (expresionStr == null || expresionStr.isBlank()) {
            throw new IllegalArgumentException("La función o expresión a evaluar no puede estar vacía.");
        }

        try {
            // 2. Normalización de notación matemática y traducción de español/inglés a Symja
            String expresionLimpia = expresionStr.trim()
                    .replace("X", "x")
                    .replaceAll("(?i)\\bsen\\b", "Sin")   // Traducción español sen(x) -> Sin(x)
                    .replaceAll("(?i)\\bsin\\b", "Sin")
                    .replaceAll("(?i)\\bcos\\b", "Cos")
                    .replaceAll("(?i)\\btan\\b", "Tan")
                    .replaceAll("(?i)\\bln\\b", "Log")    // Logaritmo natural
                    .replaceAll("(?i)\\bin\\b", "Log")
                    .replaceAll("(?i)\\blog\\b", "Log")
                    .replaceAll("(?i)\\bexp\\b", "Exp")
                    .replaceAll("(?i)e\\^", "E^");        // Notación e^x -> E^x de Symja

            // 3. Inyección del valor numérico forzando formato de EE.UU. (punto decimal)
            String comandoSymja = String.format(Locale.US, "N(ReplaceAll(%s, x -> %.12f))", expresionLimpia, valorX);

            // 4. Evaluación en el motor matemático Symja
            IExpr resultadoExpr = exprEvaluator.eval(comandoSymja);

            // 5. Conversión segura a double (convierte *10^ de Symja a E de Java)
            String cadenaNumerica = resultadoExpr.toString()
                    .replace("*10^", "E")
                    .replaceAll("\\s+", "");

            return Double.parseDouble(cadenaNumerica);

        } catch (Exception e) {
            log.error("Error al evaluar la función '{}' en x={}: {}", expresionStr, valorX, e.getMessage());
            throw new IllegalArgumentException(
                String.format("Expresión matemática no válida: '%s'. Asegúrate de escribirla correctamente en términos de 'x' (ej: sin(x), e^-x, x^2 - 5).", expresionStr)
            );
        }
    }
    @Override
    public List<PuntoFijoRespuesta> puntoFijo(PuntoFijo request) {
        List<PuntoFijoRespuesta> respuestas = new ArrayList<>();
        double x0 = request.getX0();
        double er = 100.0;

        for (int i = 1; i <= request.getMaximoIteraciones(); i++) {
            // Paso 1: Evaluación de G(X0) en Symja
            double gx0 = evaluarFuncion(request.getGx(), x0);

            // Paso 2: Cálculo del Error Relativo Porcentual (ER)
            if (i > 1) {
                if (gx0 != 0) {
                    er = Math.abs((gx0 - x0) / gx0) * 100.0;
                } else {
                    er = 0.0;
                }
            }

            // Paso 3: Construcción inmutable de la respuesta mediante Lombok @Builder
            PuntoFijoRespuesta respuesta = PuntoFijoRespuesta.builder()
                    .iteracion(i)
                    .x0(x0)
                    .gx0(gx0)
                    .er(er)
                    .build();

            respuestas.add(respuesta);

            // Paso 4: Evaluar criterio de convergencia
            if (er < request.getEr()) {
                log.info("Criterio de convergencia alcanzado en la iteración {}", i);
                break;
            }

            // Paso 5: Actualización del punto
            x0 = gx0;
        }

        // Paso 6: Retorno de colección de iteraciones
        return respuestas;
    }


    @Override
    public List<NewtonRaphsonRespuesta> newtonRaphson(NewtonRaphson request) {
        List<NewtonRaphsonRespuesta> resultados = new ArrayList<>();
        double xi = request.getXi();
        
        String funcionFx = request.getFx();
        
        // Derivada simbólica F'(x) calculada una sola vez
        String funcionDerivadaStr = exprEvaluator.eval("D(" + funcionFx + ", x)").toString();
        log.info("Función F(x): {} | Derivada F'(x): {}", funcionFx, funcionDerivadaStr);

        for (int i = 1; i <= request.getMaximoIteraciones(); i++) {
            double fxi = evaluarFuncion(funcionFx, xi);
            double fdxi = evaluarFuncion(funcionDerivadaStr, xi);

            // Control de Indeterminación por tangente horizontal (F'(Xi) -> 0)
            if (Math.abs(fdxi) < 1e-12) {
                throw new ArithmeticException(
                    String.format(Locale.US, "La derivada F'(Xi) = %.6f es cercana a cero en la iteración %d. El método diverge.", fdxi, i)
                );
            }

            double xi1 = xi - (fxi / fdxi);
            double erCalculado = (xi1 != 0) ? Math.abs((xi1 - xi) / xi1) * 100.0 : 0.0;

            NewtonRaphsonRespuesta respuesta = NewtonRaphsonRespuesta.builder()
                    .iteracion(i)
                    .xi(xi)
                    .xi1(xi1)
                    .fxi(fxi)
                    .fdxi(fdxi)
                    .er(erCalculado)
                    .build();

            resultados.add(respuesta);

            if (erCalculado < request.getEr()) {
                log.info("Convergencia alcanzada en la iteración {} con un error de {}%", i, erCalculado);
                break;
            }

            xi = xi1;
        }

        return resultados;
    }

    /**
     * Helper para evaluar expresiones matemáticas mediante Symja.
     * Sanitiza la salida en notación científica (*10^ -> E) y convierte a double de Java
     * de forma totalmente compatible con cualquier versión de Symja.
     */
    

   @Override
    public List<SecanteRespuesta> secante(Secante request) {
        List<SecanteRespuesta> resultados = new ArrayList<>();

        double xiMenos1 = request.getXiMenos1();
        double xi = request.getXi();
        String fxStr = request.getFx();
        double errorRequerido = request.getEr();
        int maxIteraciones = request.getMaximoIteraciones();

        double errorCalculado = 100.0;

        for (int i = 1; i <= maxIteraciones && errorCalculado > errorRequerido; i++) {
            // Paso 1: Evaluación de f(x_{i-1}) y f(x_i)
            double fxiMenos1 = evaluarFuncion(fxStr, xiMenos1);
            double fxi = evaluarFuncion(fxStr, xi);

            // Control defensivo contra división por cero
            double denominador = fxi - fxiMenos1;
            if (Math.abs(denominador) < 1e-12) {
                log.warn("División por cero o indeterminación en iteración {}: f(xi)={}, f(xi-1)={}", i, fxi, fxiMenos1);
                break;
            }

            // Paso 2: Cálculo de la aproximación x_{i+1} mediante la Secante
            double xiMas1 = xi - (fxi * (xi - xiMenos1)) / denominador;
            double fxiMas1 = evaluarFuncion(fxStr, xiMas1);

            // Paso 3: Cálculo del error relativo porcentual aproximado
            if (i > 1 && xiMas1 != 0) {
                errorCalculado = Math.abs((xiMas1 - xi) / xiMas1) * 100.0;
            }

            // Paso 4: Construcción inmutable del DTO de respuesta
            resultados.add(SecanteRespuesta.builder()
                    .iteracion(i)
                    .xiMenos1(xiMenos1)
                    .xi(xi)
                    .xiMas1(xiMas1)
                    .fxiMenos1(fxiMenos1)
                    .fxi(fxi)
                    .fxiMas1(fxiMas1)
                    .er(errorCalculado)
                    .build());

            // Actualización de variables para la iteración posterior
            xiMenos1 = xi;
            xi = xiMas1;
        }

        return resultados;
    }
    @Override
    public List<SecanteModificadaRespuesta> secanteModificada(SecanteModificada request) {
        log.info("Procesando algoritmo de Secante Modificada para f(x): {}", request.getFx());
        List<SecanteModificadaRespuesta> resultados = new ArrayList<>();

        double xi = request.getXi();
        double delta = request.getSigma();
        String fxStr = request.getFx();
        double errorRequerido = request.getEr();
        int maxIteraciones = request.getMaximoIteraciones();

        double errorCalculado = 100.0;

        // Paso 0: Ciclo repetitivo controlado por iteraciones
        for (int i = 1; i <= maxIteraciones; i++) {

            // Paso 1: Cálculo del punto perturbado Xi + s
            double xiMasS = xi + delta;

            // Paso 2: Evaluación de F(Xi) y F(Xi + s) mediante Symja
            double fxi = evaluarFuncion(fxStr, xi);
            double fxiMasS = evaluarFuncion(fxStr, xiMasS);

            // Paso 3: Validación de tangente horizontal / diferencia nula
            double denominador = fxiMasS - fxi;
            if (Math.abs(denominador) < 1e-12) {
                log.warn("Indeterminación en la iteración {}: |F(Xi+s) - F(Xi)| < 1e-12", i);
                throw new ArithmeticException("División por cero detectada: F(Xi+s) - F(Xi) es igual o cercano a cero. Intenta ajustar el valor de perturbación (Sigma).");
            }

            // Paso 4: Cálculo del nuevo punto Xi+1
            double xiMas1 = xi - (delta * fxi) / denominador;

            // Paso 5: Cálculo del Error Relativo Porcentual (ER)
            if (i == 1) {
                errorCalculado = 100.0;
            } else {
                errorCalculado = (xiMas1 != 0) ? Math.abs((xiMas1 - xi) / xiMas1) * 100.0 : 0.0;
            }

            // Paso 6: Construcción del DTO inmutable mediante Lombok @Builder
            resultados.add(SecanteModificadaRespuesta.builder()
                    .iteracion(i)
                    .xi(xi)
                    .xiMasS(xiMasS)
                    .xiMas1(xiMas1)
                    .fxi(fxi)
                    .fxiMasS(fxiMasS)
                    .er(errorCalculado)
                    .build());

            // Paso 7: Evaluación del criterio de convergencia
            if (i > 1 && errorCalculado < errorRequerido) {
                log.info("Secante Modificada convergió exitosamente en la iteración {} con ER = {}%", i, errorCalculado);
                break;
            }

            // Paso 8: Actualización del punto de inicio para la siguiente iteración
            xi = xiMas1;
        }

        // Paso 9: Retorno de la colección de iteraciones
        return resultados;
    }


}