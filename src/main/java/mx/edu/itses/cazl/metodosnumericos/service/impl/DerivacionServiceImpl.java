package mx.edu.itses.cazl.metodosnumericos.service.impl;

import lombok.extern.slf4j.Slf4j;
import mx.edu.itses.cazl.metodosnumericos.dto.request.derivacion.DerivacionRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.response.derivacion.DerivacionResultadoItem;
import mx.edu.itses.cazl.metodosnumericos.dto.response.derivacion.DerivacionRespuesta;
import mx.edu.itses.cazl.metodosnumericos.service.DerivacionService;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del servicio de diferenciación numérica utilizando Symja
 * para el cálculo de derivadas analíticas simbólicas y evaluación de funciones.
 */
@Slf4j
@Service
public class DerivacionServiceImpl implements DerivacionService {

    @Override
    public DerivacionRespuesta calcularDiferenciacion(DerivacionRequest request) {
        log.info("Procesando diferenciación numérica: {}", request);

        // Validaciones de entrada
        if (request.getOrdenDerivada() < 1 || request.getOrdenDerivada() > 4) {
            throw new IllegalArgumentException("El orden de la derivada debe estar entre 1 y 4.");
        }
        if (request.getH() <= 0) {
            throw new IllegalArgumentException("El tamaño de paso (h) debe ser estrictamente positivo.");
        }

        String fx = request.getFuncion();
        double x = request.getX();
        double h = request.getH();
        int orden = request.getOrdenDerivada();

        List<String> pasos = new ArrayList<>();
        pasos.add(String.format("1. Análisis inicial para f(x) = %s en x0 = %.4f, h = %.4f (Derivada Orden %d).", fx, x, h, orden));

        // 1. Obtener derivada analítica simbólica y su valor exacto en x0 con Symja
        String derivadaAnaliticaStr = obtenerDerivadaAnalitica(fx, orden);
        double valorExacto = evaluarExpresion(derivadaAnaliticaStr, x);
        pasos.add(String.format("2. Derivada simbólica f^(%d)(x) = %s", orden, derivadaAnaliticaStr));
        pasos.add(String.format("3. Valor exacto analítico f^(%d)(%.4f) = %.6f", orden, x, valorExacto));

        // 2. Evaluar puntos en f(x) requeridos según el orden
        double fx0   = evaluarExpresion(fx, x);
        double fx_p1 = evaluarExpresion(fx, x + h);
        double fx_m1 = evaluarExpresion(fx, x - h);
        double fx_p2 = evaluarExpresion(fx, x + 2 * h);
        double fx_m2 = evaluarExpresion(fx, x - 2 * h);
        double fx_p3 = evaluarExpresion(fx, x + 3 * h);
        double fx_m3 = evaluarExpresion(fx, x - 3 * h);
        double fx_p4 = evaluarExpresion(fx, x + 4 * h);
        double fx_m4 = evaluarExpresion(fx, x - 4 * h);

        pasos.add("4. Evaluación de puntos periféricos en f(x):");
        pasos.add(String.format("   f(x0) = f(%.4f) = %.6f", x, fx0));
        pasos.add(String.format("   f(x+h) = %.6f | f(x-h) = %.6f", fx_p1, fx_m1));
        pasos.add(String.format("   f(x+2h) = %.6f | f(x-2h) = %.6f", fx_p2, fx_m2));
        if (orden >= 3) {
            pasos.add(String.format("   f(x+3h) = %.6f | f(x-3h) = %.6f", fx_p3, fx_m3));
        }
        if (orden == 4) {
            pasos.add(String.format("   f(x+4h) = %.6f | f(x-4h) = %.6f", fx_p4, fx_m4));
        }

        List<DerivacionResultadoItem> resultados = new ArrayList<>();

        // 3. Aplicar formulaciones según el orden de derivada solicitado
        switch (orden) {
            case 1 -> {
                // Adelante O(h)
                double aproxAdelante = (fx_p1 - fx0) / h;
                resultados.add(crearItem("Diferencias Hacia Adelante O(h)", "[f(x+h) - f(x)] / h", aproxAdelante, valorExacto));

                // Atrás O(h)
                double aproxAtras = (fx0 - fx_m1) / h;
                resultados.add(crearItem("Diferencias Hacia Atrás O(h)", "[f(x) - f(x-h)] / h", aproxAtras, valorExacto));

                // Centrada O(h²)
                double aproxCentrada = (fx_p1 - fx_m1) / (2 * h);
                resultados.add(crearItem("Diferencias Centradas O(h²)", "[f(x+h) - f(x-h)] / (2h)", aproxCentrada, valorExacto));
            }
            case 2 -> {
                // Adelante O(h)
                double aproxAdelante = (fx_p2 - 2 * fx_p1 + fx0) / Math.pow(h, 2);
                resultados.add(crearItem("Diferencias Hacia Adelante O(h)", "[f(x+2h) - 2f(x+h) + f(x)] / h²", aproxAdelante, valorExacto));

                // Atrás O(h)
                double aproxAtras = (fx0 - 2 * fx_m1 + fx_m2) / Math.pow(h, 2);
                resultados.add(crearItem("Diferencias Hacia Atrás O(h)", "[f(x) - 2f(x-h) + f(x-2h)] / h²", aproxAtras, valorExacto));

                // Centrada O(h²)
                double aproxCentrada = (fx_p1 - 2 * fx0 + fx_m1) / Math.pow(h, 2);
                resultados.add(crearItem("Diferencias Centradas O(h²)", "[f(x+h) - 2f(x) + f(x-h)] / h²", aproxCentrada, valorExacto));
            }
            case 3 -> {
                // Adelante O(h)
                double aproxAdelante = (fx_p3 - 3 * fx_p2 + 3 * fx_p1 - fx0) / Math.pow(h, 3);
                resultados.add(crearItem("Diferencias Hacia Adelante O(h)", "[f(x+3h) - 3f(x+2h) + 3f(x+h) - f(x)] / h³", aproxAdelante, valorExacto));

                // Atrás O(h)
                double aproxAtras = (fx0 - 3 * fx_m1 + 3 * fx_m2 - fx_m3) / Math.pow(h, 3);
                resultados.add(crearItem("Diferencias Hacia Atrás O(h)", "[f(x) - 3f(x-h) + 3f(x-2h) - f(x-3h)] / h³", aproxAtras, valorExacto));

                // Centrada O(h²)
                double aproxCentrada = (fx_p2 - 2 * fx_p1 + 2 * fx_m1 - fx_m2) / (2 * Math.pow(h, 3));
                resultados.add(crearItem("Diferencias Centradas O(h²)", "[f(x+2h) - 2f(x+h) + 2f(x-h) - f(x-2h)] / (2h³)", aproxCentrada, valorExacto));
            }
            case 4 -> {
                // Adelante O(h)
                double aproxAdelante = (fx_p4 - 4 * fx_p3 + 6 * fx_p2 - 4 * fx_p1 + fx0) / Math.pow(h, 4);
                resultados.add(crearItem("Diferencias Hacia Adelante O(h)", "[f(x+4h) - 4f(x+3h) + 6f(x+2h) - 4f(x+h) + f(x)] / h⁴", aproxAdelante, valorExacto));

                // Atrás O(h)
                double aproxAtras = (fx0 - 4 * fx_m1 + 6 * fx_m2 - 4 * fx_m3 + fx_m4) / Math.pow(h, 4);
                resultados.add(crearItem("Diferencias Hacia Atrás O(h)", "[f(x) - 4f(x-h) + 6f(x-2h) - 4f(x-3h) + f(x-4h)] / h⁴", aproxAtras, valorExacto));

                // Centrada O(h²)
                double aproxCentrada = (fx_p2 - 4 * fx_p1 + 6 * fx0 - 4 * fx_m1 + fx_m2) / Math.pow(h, 4);
                resultados.add(crearItem("Diferencias Centradas O(h²)", "[f(x+2h) - 4f(x+h) + 6f(x) - 4f(x-h) + f(x-2h)] / h⁴", aproxCentrada, valorExacto));
            }
        }

        pasos.add("5. Cálculo de aproximaciones y errores relativos completado exitosamente.");

        return DerivacionRespuesta.builder()
                .funcion(fx)
                .x(x)
                .h(h)
                .ordenDerivada(orden)
                .derivadaAnalitica(derivadaAnaliticaStr)
                .valorExacto(valorExacto)
                .resultados(resultados)
                .pasosDesarrollo(pasos)
                .build();
    }

    private DerivacionResultadoItem crearItem(String esquema, String formula, double aprox, double exacto) {
        double error = (exacto != 0) ? Math.abs((exacto - aprox) / exacto) * 100.0 : 0.0;
        return DerivacionResultadoItem.builder()
                .esquema(esquema)
                .formulaUsada(formula)
                .valorAproximado(aprox)
                .errorRelativo(error)
                .build();
    }

    private String obtenerDerivadaAnalitica(String funcion, int orden) {
        try {
            ExprEvaluator util = new ExprEvaluator();
            IExpr expr = util.eval(String.format("D(%s, {x, %d})", funcion, orden));
            return expr.toString();
        } catch (Exception e) {
            log.error("Error derivando simbólicamente f(x)='{}': {}", funcion, e.getMessage());
            throw new IllegalArgumentException("La expresión ingresada no se pudo derivar simbólicamente.");
        }
    }

    private double evaluarExpresion(String expresion, double valX) {
        try {
            ExprEvaluator util = new ExprEvaluator();
            // N(...) fuerza la evaluación de la expresión a un valor numérico decimal
            String query = String.format("N(ReplaceAll(%s, x -> (%s)))", expresion, String.valueOf(valX));
            IExpr res = util.eval(query);
            
            // Al estar forzado a número con N(), se puede parsear directamente como Double
            return Double.parseDouble(res.toString());
            
        } catch (NumberFormatException nfe) {
            log.error("El resultado de '{}' en x={} no es un número válido: {}", expresion, valX, nfe.getMessage());
            throw new IllegalArgumentException("No se pudo obtener un valor numérico de la función en x = " + valX);
        } catch (Exception e) {
            log.error("Error al evaluar '{}' en x={}: {}", expresion, valX, e.getMessage());
            throw new IllegalArgumentException("Error al evaluar la función en el punto x = " + valX);
        }
    }
}