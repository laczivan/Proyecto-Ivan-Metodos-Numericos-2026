package mx.edu.itses.cazl.metodosnumericos.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.itses.cazl.metodosnumericos.dto.request.sistemasnolineales.NewtonRaphsonNoLinealRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.sistemasnolineales.PuntoFijoNoLinealRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemasnolineales.NewtonRaphsonNoLinealIteracion;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemasnolineales.NewtonRaphsonNoLinealRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemasnolineales.PuntoFijoNoLinealIteracion;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemasnolineales.PuntoFijoNoLinealRespuesta;
import mx.edu.itses.cazl.metodosnumericos.service.SistemasNoLinealesService;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class SistemasNoLinealesServiceImpl implements SistemasNoLinealesService {

    private final ExprEvaluator exprEvaluator;

    @Override
    public PuntoFijoNoLinealRespuesta resolverPuntoFijo(PuntoFijoNoLinealRequest request) {
        log.info("Iniciando Punto Fijo No Lineal para g1: '{}', g2: '{}', X0: {}, Y0: {}",
                request.getG1(), request.getG2(), request.getX0(), request.getY0());

        if (request.getG1() == null || request.getG1().isBlank() ||
            request.getG2() == null || request.getG2().isBlank()) {
            throw new IllegalArgumentException("Las funciones despejadas g1(x, y) y g2(x, y) son requeridas.");
        }

        List<PuntoFijoNoLinealIteracion> iteraciones = new ArrayList<>();
        List<String> observaciones = new ArrayList<>();

        double xActual = request.getX0();
        double yActual = request.getY0();
        boolean convergio = false;

        for (int k = 1; k <= request.getMaximoIteraciones(); k++) {
            // Evaluación simultánea de g1(x, y) y g2(x, y)
            double g1Eval = evaluarFuncionBiVariable(request.getG1(), xActual, yActual);
            double g2Eval = evaluarFuncionBiVariable(request.getG2(), xActual, yActual);

            double xSiguiente = g1Eval;
            double ySiguiente = g2Eval;

            // Cálculo de errores relativos porcentuales
            double errorX = (xSiguiente != 0.0) 
                    ? Math.abs((xSiguiente - xActual) / xSiguiente) * 100.0 
                    : Math.abs(xSiguiente - xActual);

            double errorY = (ySiguiente != 0.0) 
                    ? Math.abs((ySiguiente - yActual) / ySiguiente) * 100.0 
                    : Math.abs(ySiguiente - yActual);

            double errorMaximo = Math.max(errorX, errorY);

            iteraciones.add(PuntoFijoNoLinealIteracion.builder()
                    .iteracion(k)
                    .x(xActual)
                    .y(yActual)
                    .g1Evaluado(g1Eval)
                    .g2Evaluado(g2Eval)
                    .errorX(errorX)
                    .errorY(errorY)
                    .errorMaximo(errorMaximo)
                    .build());

            if (errorMaximo < request.getTolerancia()) {
                convergio = true;
                observaciones.add(String.format(Locale.US, "El método convergió exitosamente en la iteración %d con un error máximo de %.6f%%.", k, errorMaximo));
                xActual = xSiguiente;
                yActual = ySiguiente;
                break;
            }

            xActual = xSiguiente;
            yActual = ySiguiente;
        }

        if (!convergio) {
            observaciones.add(String.format(Locale.US, "Se alcanzó el límite máximo de %d iteraciones sin cumplir la tolerancia (%.6f%%). El sistema puede ser divergente para los despejes dados.",
                    request.getMaximoIteraciones(), request.getTolerancia()));
        }

        return PuntoFijoNoLinealRespuesta.builder()
                .g1Original(request.getG1())
                .g2Original(request.getG2())
                .x0(request.getX0())
                .y0(request.getY0())
                .solucionX(xActual)
                .solucionY(yActual)
                .iteraciones(iteraciones)
                .convergio(convergio)
                .observaciones(observaciones)
                .build();
    }

    private double evaluarFuncionBiVariable(String expresionStr, double valorX, double valorY) {
        try {
            String expresionLimpia = expresionStr.trim()
                    .replace("X", "x")
                    .replace("Y", "y")
                    .replaceAll("(?i)\\bsen\\b", "Sin")
                    .replaceAll("(?i)\\bsin\\b", "Sin")
                    .replaceAll("(?i)\\bcos\\b", "Cos")
                    .replaceAll("(?i)\\btan\\b", "Tan")
                    .replaceAll("(?i)\\bln\\b", "Log")
                    .replaceAll("(?i)\\blog\\b", "Log")
                    .replaceAll("(?i)\\bexp\\b", "Exp")
                    .replaceAll("(?i)e\\^", "E^");

            String comandoSymja = String.format(Locale.US,
                    "N(ReplaceAll(ReplaceAll(%s, x -> %.12f), y -> %.12f))",
                    expresionLimpia, valorX, valorY);

            IExpr resultadoExpr = exprEvaluator.eval(comandoSymja);
            String cadenaNumerica = resultadoExpr.toString()
                    .replace("*10^", "E")
                    .replaceAll("\\s+", "");

            return Double.parseDouble(cadenaNumerica);
        } catch (Exception e) {
            log.error("Error al evaluar expresión bi-variable '{}' para x={}, y={}: {}", expresionStr, valorX, valorY, e.getMessage());
            throw new IllegalArgumentException(
                    String.format("Expresión no válida: '%s'. Escríbela en términos de 'x' e 'y' (ej. (5 - y^2)/2).", expresionStr)
            );
        }
    }
    @Override
    public NewtonRaphsonNoLinealRespuesta resolverNewtonRaphson(NewtonRaphsonNoLinealRequest request) {
        String f1 = request.getF1();
        String f2 = request.getF2();

        if (f1 == null || f1.isBlank() || f2 == null || f2.isBlank()) {
            throw new IllegalArgumentException("Las funciones f1(x, y) y f2(x, y) son obligatorias.");
        }

        // 1. Derivación simbólica automática con Symja para el Jacobiano
        String df1dxStr = obtenerDerivadaParcial(f1, "x");
        String df1dyStr = obtenerDerivadaParcial(f1, "y");
        String df2dxStr = obtenerDerivadaParcial(f2, "x");
        String df2dyStr = obtenerDerivadaParcial(f2, "y");

        log.info("Derivadas parciales -> df1/dx: {}, df1/dy: {}, df2/dx: {}, df2/dy: {}",
                df1dxStr, df1dyStr, df2dxStr, df2dyStr);

        List<NewtonRaphsonNoLinealIteracion> iteraciones = new ArrayList<>();
        List<String> observaciones = new ArrayList<>();

        double xActual = request.getX0();
        double yActual = request.getY0();
        boolean convergio = false;

        // 2. Proceso Iterativo
        for (int k = 1; k <= request.getMaximoIteraciones(); k++) {
            // Evaluación de las funciones en el punto (x, y)
            double f1Eval = evaluarBiVariable(f1, xActual, yActual);
            double f2Eval = evaluarBiVariable(f2, xActual, yActual);

            // Evaluación de las derivadas parciales en el punto (x, y)
            double j11 = evaluarBiVariable(df1dxStr, xActual, yActual); // df1/dx
            double j12 = evaluarBiVariable(df1dyStr, xActual, yActual); // df1/dy
            double j21 = evaluarBiVariable(df2dxStr, xActual, yActual); // df2/dx
            double j22 = evaluarBiVariable(df2dyStr, xActual, yActual); // df2/dy

            // Cálculo del Determinante Jacobiano J
            double jacobiano = (j11 * j22) - (j12 * j21);

            // Control de indeterminación por Jacobiano nulo
            if (Math.abs(jacobiano) < 1e-12) {
                throw new ArithmeticException(String.format(Locale.US,
                        "El determinante Jacobiano es cercano a cero (J = %.8f) en la iteración %d. El sistema diverge.",
                        jacobiano, k));
            }

            // Inversión mediante Regla de Cramer para resolver J * [deltaX, deltaY]^T = -[f1, f2]^T
            double deltaX = (f2Eval * j12 - f1Eval * j22) / jacobiano;
            double deltaY = (f1Eval * j21 - f2Eval * j11) / jacobiano;

            double xSiguiente = xActual + deltaX;
            double ySiguiente = yActual + deltaY;

            // Cálculo de errores relativos porcentuales
            double errorX = (xSiguiente != 0.0)
                    ? Math.abs((xSiguiente - xActual) / xSiguiente) * 100.0
                    : Math.abs(xSiguiente - xActual);

            double errorY = (ySiguiente != 0.0)
                    ? Math.abs((ySiguiente - yActual) / ySiguiente) * 100.0
                    : Math.abs(ySiguiente - yActual);

            double errorMaximo = Math.max(errorX, errorY);

            // Registro del paso iterativo
            iteraciones.add(NewtonRaphsonNoLinealIteracion.builder()
                    .iteracion(k)
                    .x(xActual)
                    .y(yActual)
                    .f1Evaluado(f1Eval)
                    .f2Evaluado(f2Eval)
                    .jacobiano(jacobiano)
                    .errorX(errorX)
                    .errorY(errorY)
                    .errorMaximo(errorMaximo)
                    .build());

            // Verificación del criterio de parada
            if (errorMaximo < request.getTolerancia()) {
                convergio = true;
                observaciones.add(String.format(Locale.US,
                        "El método de Newton-Raphson convergió exitosamente en la iteración %d con un error máximo de %.6f%%.",
                        k, errorMaximo));
                xActual = xSiguiente;
                yActual = ySiguiente;
                break;
            }

            xActual = xSiguiente;
            yActual = ySiguiente;
        }

        if (!convergio) {
            observaciones.add(String.format(Locale.US,
                    "Se alcanzó el límite de %d iteraciones sin cumplir con la tolerancia especificada (%.6f%%).",
                    request.getMaximoIteraciones(), request.getTolerancia()));
        }

        return NewtonRaphsonNoLinealRespuesta.builder()
                .f1Original(f1)
                .f2Original(f2)
                .df1dxStr(df1dxStr)
                .df1dyStr(df1dyStr)
                .df2dxStr(df2dxStr)
                .df2dyStr(df2dyStr)
                .x0(request.getX0())
                .y0(request.getY0())
                .solucionX(xActual)
                .solucionY(yActual)
                .iteraciones(iteraciones)
                .convergio(convergio)
                .observaciones(observaciones)
                .build();
    }

    private String obtenerDerivadaParcial(String funcionStr, String variable) {
        try {
            String funcionLimpia = sanitizarExpresion(funcionStr);
            String comando = String.format("D(%s, %s)", funcionLimpia, variable);
            return exprEvaluator.eval(comando).toString();
        } catch (Exception e) {
            log.error("Error al derivar parcialmente '{}' respecto a {}: {}", funcionStr, variable, e.getMessage());
            throw new IllegalArgumentException("No se pudo obtener la derivada parcial de: " + funcionStr);
        }
    }

    private double evaluarBiVariable(String expresionStr, double valorX, double valorY) {
        try {
            String expresionLimpia = sanitizarExpresion(expresionStr);
            String comandoSymja = String.format(Locale.US,
                    "N(ReplaceAll(ReplaceAll(%s, x -> %.12f), y -> %.12f))",
                    expresionLimpia, valorX, valorY);

            IExpr resultadoExpr = exprEvaluator.eval(comandoSymja);
            String cadenaNumerica = resultadoExpr.toString()
                    .replace("*10^", "E")
                    .replaceAll("\\s+", "");

            return Double.parseDouble(cadenaNumerica);
        } catch (Exception e) {
            log.error("Error evaluando expresión '{}' en x={}, y={}: {}", expresionStr, valorX, valorY, e.getMessage());
            throw new IllegalArgumentException("Error al evaluar expresión matemática bi-variable.");
        }
    }

    private String sanitizarExpresion(String expresionStr) {
        return expresionStr.trim()
                .replace("X", "x")
                .replace("Y", "y")
                .replaceAll("(?i)\\bsen\\b", "Sin")
                .replaceAll("(?i)\\bsin\\b", "Sin")
                .replaceAll("(?i)\\bcos\\b", "Cos")
                .replaceAll("(?i)\\btan\\b", "Tan")
                .replaceAll("(?i)\\bln\\b", "Log")
                .replaceAll("(?i)\\blog\\b", "Log")
                .replaceAll("(?i)\\bexp\\b", "Exp")
                .replaceAll("(?i)e\\^", "E^");
    }
}