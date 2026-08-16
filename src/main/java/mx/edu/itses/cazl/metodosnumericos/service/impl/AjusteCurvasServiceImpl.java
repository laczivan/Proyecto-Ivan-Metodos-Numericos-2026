package mx.edu.itses.cazl.metodosnumericos.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import mx.edu.itses.cazl.metodosnumericos.service.AjusteCurvasService;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class AjusteCurvasServiceImpl implements AjusteCurvasService {

    @Override
    public DiferenciasDivididasRespuesta diferenciasDivididas(DiferenciasDivididasRequest request) {
        int n = request.getNumeroPuntos();

        // 1. Validación Fail-Fast: Grado máximo 4 (máximo 5 puntos, mínimo 2 puntos)
        if (n < 2 || n > 5) {
            throw new IllegalArgumentException("El método requiere entre 2 y 5 puntos (Grado máximo 4).");
        }

        List<Double> x = request.getValoresX();
        List<Double> y = request.getValoresY();

        if (x == null || y == null || x.size() != n || y.size() != n) {
            throw new IllegalArgumentException("Las dimensiones de los vectores X y Y deben coincidir con el número de puntos (" + n + ").");
        }

        // Validación de puntos X duplicados (división por cero)
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(x.get(i) - x.get(j)) < 1e-12) {
                    throw new IllegalArgumentException(String.format(Locale.US, "Los valores de X deben ser distintos entre sí. Conflicto en x[%d] y x[%d] = %.4f", i, j, x.get(i)));
                }
            }
        }

        List<String> pasos = new ArrayList<>();
        pasos.add(String.format("Construyendo tabla de Diferencias Divididas para %d puntos (Polinomio de Orden %d):", n, n - 1));

        // 2. Construcción de la tabla de diferencias divididas (Matriz NxN)
        double[][] tabla = new double[n][n];
        for (int i = 0; i < n; i++) {
            tabla[i][0] = y.get(i);
        }

        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                double numerador = tabla[i + 1][j - 1] - tabla[i][j - 1];
                double denominador = x.get(i + j) - x.get(i);
                tabla[i][j] = numerador / denominador;

                pasos.add(String.format(Locale.US, "f[x_%d...x_%d] = (%.4f - %.4f) / (%.4f - %.4f) = %.4f",
                        i, i + j, tabla[i + 1][j - 1], tabla[i][j - 1], x.get(i + j), x.get(i), tabla[i][j]));
            }
        }

        // 3. Extracción de Coeficientes b_k (Primera fila de la tabla)
        List<Double> coeficientesB = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            coeficientesB.add(tabla[0][j]);
        }

        // 4. Construcción simbólica de la ecuación del polinomio
        StringBuilder polyStr = new StringBuilder();
        polyStr.append(String.format(Locale.US, "P(x) = %.4f", coeficientesB.get(0)));
        for (int k = 1; k < n; k++) {
            double b = coeficientesB.get(k);
            polyStr.append(b >= 0 ? " + " : " - ");
            polyStr.append(String.format(Locale.US, "%.4f", Math.abs(b)));
            for (int j = 0; j < k; j++) {
                polyStr.append(String.format(Locale.US, "(x - %.4f)", x.get(j)));
            }
        }

        // 5. Evaluación opcional en xEvaluar
        Double xEval = request.getXEvaluar();
        Double resultadoEval = null;
        if (xEval != null) {
            resultadoEval = coeficientesB.get(0);
            for (int k = 1; k < n; k++) {
                double termino = coeficientesB.get(k);
                for (int j = 0; j < k; j++) {
                    termino *= (xEval - x.get(j));
                }
                resultadoEval += termino;
            }
            pasos.add(String.format(Locale.US, "Evaluación P(%.4f) = %.6f", xEval, resultadoEval));
        }

        log.info("Diferencias divididas procesadas con éxito para {} puntos.", n);

        return DiferenciasDivididasRespuesta.builder()
                .numeroPuntos(n)
                .gradoPolinomio(n - 1)
                .valoresX(x)
                .valoresY(y)
                .tablaDiferencias(tabla)
                .coeficientesB(coeficientesB)
                .polinomioResultante(polyStr.toString())
                .xEvaluar(xEval)
                .resultadoEvaluacion(resultadoEval)
                .pasosDesarrollo(pasos)
                .build();
    }
    private final ExprEvaluator exprEvaluator;
    @Override
    public LagrangeRespuesta lagrange(LagrangeRequest request) {
        log.info("Iniciando algoritmo de Polinomio de Lagrange con {} puntos.", request.getNumeroPuntos());

        int n = request.getNumeroPuntos();
        List<Double> x = request.getValoresX();
        List<Double> y = request.getValoresY();
        Double xEval = request.getXEvaluar();

        // 1. Validación Fail-Fast
        if (n < 2 || n > 5) {
            throw new IllegalArgumentException("El método admite de 2 a 5 puntos (grado máximo de orden 4).");
        }
        if (x == null || y == null || x.size() != n || y.size() != n) {
            throw new IllegalArgumentException("La cantidad de valores en X y Y debe coincidir con el número de puntos ingresados.");
        }

        List<String> pasos = new ArrayList<>();
        List<String> polinomiosBase = new ArrayList<>();
        StringBuilder polinomioCompletoExpr = new StringBuilder();

        pasos.add(String.format("Calculando Polinomio de Lagrange para N = %d puntos (Grado máximo = %d):", n, n - 1));

        // 2. Construcción de funciones base L_i(x)
        for (int i = 0; i < n; i++) {
            StringBuilder numerador = new StringBuilder();
            StringBuilder denominador = new StringBuilder();
            double denominadorVal = 1.0;

            for (int j = 0; j < n; j++) {
                if (i != j) {
                    numerador.append(String.format(Locale.US, "(x - (%.4f))*", x.get(j)));
                    denominador.append(String.format(Locale.US, "(%.4f - (%.4f))*", x.get(i), x.get(j)));
                    denominadorVal *= (x.get(i) - x.get(j));
                }
            }

            // Eliminar último asterisco
            String numStr = numerador.substring(0, numerador.length() - 1);
            String denStr = denominador.substring(0, denominador.length() - 1);

            if (Math.abs(denominadorVal) < 1e-12) {
                throw new ArithmeticException(String.format("Existen valores repetidos en X (X[%d] = X). No se puede dividir por cero.", i));
            }

            String liStr = String.format(Locale.US, "(%s) / %.4f", numStr, denominadorVal);
            polinomiosBase.add(String.format("L_%d(x) = %s", i, liStr));

            pasos.add(String.format(Locale.US, "L_%d(x) = [%s] / [%s] = (%s) / %.4f", i, numStr, denStr, numStr, denominadorVal));

            // Acumular término y_i * L_i(x)
            polinomioCompletoExpr.append(String.format(Locale.US, "(%.4f) * ((%s) / %.4f) + ", y.get(i), numStr, denominadorVal));
        }

        // Remueve el último '+'
        String funcionSinSimplificar = polinomioCompletoExpr.substring(0, polinomioCompletoExpr.length() - 3);

        // 3. Simplificación simbólica mediante Symja
        IExpr exprSimplificada = exprEvaluator.eval(String.format("Expand(%s)", funcionSinSimplificar));
        String polinomioResultante = exprSimplificada.toString().replace("*", "·");

        pasos.add("\nPolinomio Interpolador Resultante P(x):");
        pasos.add(String.format("P(x) = %s", polinomioResultante));

        // 4. Evaluación en punto xTarget si fue provisto
        Double yEvaluado = null;
        if (xEval != null) {
            String comandoEval = String.format(Locale.US, "N(ReplaceAll(%s, x -> %.12f))", exprSimplificada.toString(), xEval);
            IExpr evalRes = exprEvaluator.eval(comandoEval);
            yEvaluado = Double.parseDouble(evalRes.toString().replace("*10^", "E"));
            pasos.add(String.format(Locale.US, "\nEvaluación en x = %.4f: P(%.4f) = %.6f", xEval, xEval, yEvaluado));
        }

        log.info("Polinomio de Lagrange calculado con éxito: P(x) = {}", polinomioResultante);

        return LagrangeRespuesta.builder()
                .numeroPuntos(n)
                .gradoPolinomio(n - 1)
                .valoresX(x)
                .valoresY(y)
                .polinomiosBaseL(polinomiosBase)
                .polinomioResultante(polinomioResultante)
                .xEvaluar(xEval)
                .yEvaluado(yEvaluado)
                .pasosDesarrollo(pasos)
                .build();
    }
    @Override
    public RegresionLinealRespuesta regresionLineal(RegresionLinealRequest request) {
        int n = request.getNumeroPuntos() != null ? request.getNumeroPuntos() : 0;
        List<Double> x = request.getValoresX();
        List<Double> y = request.getValoresY();

        // 1. Validación Fail-Fast
        if (n < 2) {
            throw new IllegalArgumentException("El número de puntos (N) debe ser mayor o igual a 2.");
        }
        if (x == null || y == null || x.size() != n || y.size() != n) {
            throw new IllegalArgumentException("Los vectores X y Y deben coincidir con la cantidad de puntos especificada.");
        }

        log.info("Procesando Regresión Lineal para N = {} puntos", n);

        // 2. Cálculo acumulativo de sumatorias
        double sumX = 0.0;
        double sumY = 0.0;
        double sumX2 = 0.0;
        double sumXY = 0.0;
        double sumY2 = 0.0;

        for (int i = 0; i < n; i++) {
            double xi = x.get(i);
            double yi = y.get(i);
            sumX += xi;
            sumY += yi;
            sumX2 += (xi * xi);
            sumXY += (xi * yi);
            sumY2 += (yi * yi);
        }

        // 3. Promedios
        double promX = sumX / n;
        double promY = sumY / n;

        List<String> pasos = new ArrayList<>();
        pasos.add(String.format("1. Cálculo de sumatorias acumuladas para N = %d puntos:", n));
        pasos.add(String.format(Locale.US, "   • ΣX = %.4f | ΣY = %.4f | Σ(X²) = %.4f | Σ(XY) = %.4f | Σ(Y²) = %.4f", 
                sumX, sumY, sumX2, sumXY, sumY2));
        pasos.add(String.format(Locale.US, "   • Promedio X (X̅) = %.4f / %d = %.4f", sumX, n, promX));
        pasos.add(String.format(Locale.US, "   • Promedio Y (Y̅) = %.4f / %d = %.4f", sumY, n, promY));

        // 4. Cálculo de la Pendiente (a1)
        double denA1 = (n * sumX2) - (sumX * sumX);
        if (Math.abs(denA1) < 1e-12) {
            throw new ArithmeticException("División por cero en la pendiente (a1). Los valores de X son idénticos o verticales.");
        }

        double numA1 = (n * sumXY) - (sumX * sumY);
        double a1 = numA1 / denA1;

        pasos.add("2. Cálculo de la pendiente (a1):");
        pasos.add(String.format(Locale.US, "   a1 = [ N · Σ(XY) - (ΣX)(ΣY) ] / [ N · Σ(X²) - (ΣX)² ]"));
        pasos.add(String.format(Locale.US, "   a1 = [ %d · %.4f - (%.4f)(%.4f) ] / [ %d · %.4f - (%.4f)² ]", 
                n, sumXY, sumX, sumY, n, sumX2, sumX));
        pasos.add(String.format(Locale.US, "   a1 = %.4f / %.4f = %.4f", numA1, denA1, a1));

        // 5. Cálculo del Intercepto (a0)
        double a0 = promY - (a1 * promX);

        pasos.add("3. Cálculo del término independiente / intercepto (a0):");
        pasos.add(String.format(Locale.US, "   a0 = Y̅ - a1 · X̅ = %.4f - (%.4f · %.4f) = %.4f", promY, a1, promX, a0));

        // 6. Coeficientes de Correlación (r) y Determinación (r²)
        double denRTermX = (n * sumX2) - (sumX * sumX);
        double denRTermY = (n * sumY2) - (sumY * sumY);
        double denR = Math.sqrt(denRTermX * denRTermY);

        double r = 0.0;
        double r2 = 0.0;

        if (denR > 1e-12) {
            r = numA1 / denR;
            r2 = r * r;
        }

        pasos.add("4. Cálculo de Coeficientes de Ajuste:");
        pasos.add(String.format(Locale.US, "   • Coeficiente de Correlación (r) = %.4f", r));
        pasos.add(String.format(Locale.US, "   • Coeficiente de Determinación (r²) = %.4f (%.2f%% de varianza explicada)", r2, r2 * 100));

        // 7. Formateo de la ecuación de la recta
        String signo = a1 >= 0 ? " + " : " - ";
        String ecuacion = String.format(Locale.US, "y = %.4f%s%.4fx", a0, signo, Math.abs(a1));

        return RegresionLinealRespuesta.builder()
                .numeroPuntos(n)
                .sumatoriaX(sumX)
                .sumatoriaY(sumY)
                .sumatoriaX2(sumX2)
                .sumatoriaXY(sumXY)
                .promedioX(promX)
                .promedioY(promY)
                .a0(a0)
                .a1(a1)
                .coeficienteCorrelacionR(r)
                .coeficienteDeterminacionR2(r2)
                .ecuacionResultante(ecuacion)
                .pasosDesarrollo(pasos)
                .build();
    }
    @Override
    public RegresionPolinomialRespuesta regresionPolinomial(RegresionPolinomialRequest request) {
        log.info("Procesando Regresión Polinomial de Grado {} con {} puntos", request.getGrado(), request.getNumeroPuntos());
        
        int m = request.getGrado();
        List<Double> x = request.getValoresX();
        List<Double> y = request.getValoresY();

        // 1. Validaciones Fail-Fast
        if (m < 2 || m > 4) {
            throw new IllegalArgumentException("El grado del polinomio debe estar comprendido entre 2 y 4.");
        }
        if (x == null || y == null || x.size() != y.size()) {
            throw new IllegalArgumentException("Los vectores de coordenadas X y Y deben poseer el mismo número de elementos.");
        }
        int n = x.size();
        if (n < (m + 1)) {
            throw new IllegalArgumentException(String.format("Para un polinomio de grado %d se requieren al menos %d puntos de datos.", m, m + 1));
        }

        List<String> pasos = new ArrayList<>();
        pasos.add(String.format(Locale.US, "Grado del polinomio seleccionado: m = %d. Cantidad de datos: N = %d.", m, n));

        // 2. Cálculo de sumatorias de potencias de X y productos X^k * Y
        double[] sumX = new double[2 * m + 1];
        double[] sumXY = new double[m + 1];

        for (int i = 0; i < n; i++) {
            double xi = x.get(i);
            double yi = y.get(i);

            for (int k = 0; k <= 2 * m; k++) {
                sumX[k] += Math.pow(xi, k);
            }
            for (int k = 0; k <= m; k++) {
                sumXY[k] += (Math.pow(xi, k) * yi);
            }
        }

        pasos.add("Sumatorias de potencias de X calculadas desde x^0 hasta x^" + (2 * m));
        pasos.add("Sumatorias de productos X^k * Y calculadas desde x^0*y hasta x^" + m + "*y");

        // 3. Construcción del sistema de ecuaciones normales de dimensión (m+1) x (m+2)
        int numEq = m + 1;
        double[][] matrizAumentada = new double[numEq][numEq + 1];

        for (int i = 0; i < numEq; i++) {
            for (int j = 0; j < numEq; j++) {
                matrizAumentada[i][j] = sumX[i + j];
            }
            matrizAumentada[i][numEq] = sumXY[i];
        }

        // Copia para preservar el estado original de la matriz aumentada en el DTO
        double[][] matrizSistemaCopia = new double[numEq][numEq + 1];
        for (int i = 0; i < numEq; i++) {
            System.arraycopy(matrizAumentada[i], 0, matrizSistemaCopia[i], 0, numEq + 1);
        }

        pasos.add("Sistema de Ecuaciones Normales construido de dimensión (" + numEq + "x" + (numEq + 1) + ").");

        // 4. Resolución del sistema por Eliminación Gaussiana con Pivoteo Parcial
        double[] coeficientes = resolverSistemaGauss(matrizAumentada, numEq, pasos);

        // 5. Cálculo de Estadísticos de Calidad de Ajuste (St, Sr, R2, R y Sy/x)
        double sumY = sumXY[0];
        double promedioY = sumY / n;
        double st = 0.0;
        double sr = 0.0;

        for (int i = 0; i < n; i++) {
            double xi = x.get(i);
            double yi = y.get(i);

            // Evaluación del polinomio P(xi) = a0 + a1*x + a2*x^2 + ... + am*x^m
            double yPredicho = 0.0;
            for (int k = 0; k <= m; k++) {
                yPredicho += coeficientes[k] * Math.pow(xi, k);
            }

            st += Math.pow(yi - promedioY, 2);
            sr += Math.pow(yi - yPredicho, 2);
        }

        double r2 = (st != 0) ? (st - sr) / st : 0.0;
        double r = Math.sqrt(Math.max(0.0, r2));
        double syx = (n > numEq) ? Math.sqrt(sr / (n - numEq)) : 0.0;

        pasos.add(String.format(Locale.US, "Suma total de cuadrados (St): %.6f", st));
        pasos.add(String.format(Locale.US, "Suma de residuos al cuadrado (Sr): %.6f", sr));
        pasos.add(String.format(Locale.US, "Coeficiente de Determinación (R²): %.6f", r2));
        pasos.add(String.format(Locale.US, "Coeficiente de Correlación (R): %.6f", r));
        pasos.add(String.format(Locale.US, "Error estándar de la estimación (Sy/x): %.6f", syx));

        // 6. Construcción de la cadena que representa la ecuación polinomial ajustada
        StringBuilder sbEcuacion = new StringBuilder("y = ");
        for (int i = 0; i <= m; i++) {
            double coef = coeficientes[i];
            if (i == 0) {
                sbEcuacion.append(String.format(Locale.US, "%.6f", coef));
            } else {
                sbEcuacion.append(coef >= 0 ? " + " : " - ");
                sbEcuacion.append(String.format(Locale.US, "%.6f", Math.abs(coef)));
                sbEcuacion.append("x");
                if (i > 1) {
                    sbEcuacion.append("^").append(i);
                }
            }
        }

        return RegresionPolinomialRespuesta.builder()
                .grado(m)
                .coeficientes(coeficientes)
                .sumatoriasX(sumX)
                .sumatoriasXY(sumXY)
                .matrizSistema(matrizSistemaCopia)
                .coeficienteDeterminacionR2(r2)
                .coeficienteCorrelacionR(r)
                .errorEstandar(syx)
                .ecuacionResultante(sbEcuacion.toString())
                .pasosDesarrollo(pasos)
                .build();
    }

    private double[] resolverSistemaGauss(double[][] a, int n, List<String> pasos) {
        pasos.add("Iniciando eliminación Gaussiana con pivoteo parcial...");
        for (int i = 0; i < n; i++) {
            int maxFila = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(a[k][i]) > Math.abs(a[maxFila][i])) {
                    maxFila = k;
                }
            }
            if (maxFila != i) {
                double[] temp = a[i];
                a[i] = a[maxFila];
                a[maxFila] = temp;
                pasos.add(String.format(Locale.US, "Intercambio de Fila %d con Fila %d", i + 1, maxFila + 1));
            }

            if (Math.abs(a[i][i]) < 1e-12) {
                throw new ArithmeticException("El sistema de ecuaciones es singular o indeterminado; pivote nulo detectado.");
            }

            for (int j = i + 1; j < n; j++) {
                double factor = a[j][i] / a[i][i];
                for (int k = i; k <= n; k++) {
                    a[j][k] -= factor * a[i][k];
                }
            }
        }

        // Sustitución hacia atrás
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double suma = 0.0;
            for (int j = i + 1; j < n; j++) {
                suma += a[i][j] * x[j];
            }
            x[i] = (a[i][n] - suma) / a[i][i];
        }
        pasos.add("Sustitución hacia atrás completada. Coeficientes obtenidos.");
        return x;
    }
    @Override
    public RegresionMultipleRespuesta regresionMultiple(RegresionMultipleRequest request) {
        log.info("Ejecutando algoritmo de Regresión Lineal Múltiple para N={}", request.getNumeroPuntos());
        
        int n = request.getNumeroPuntos();
        double[] x1 = request.getValoresX1();
        double[] x2 = request.getValoresX2();
        double[] y = request.getValoresY();

        // 21.6 Validación Fail-Fast
        if (n < 4) {
            throw new IllegalArgumentException("La regresión lineal múltiple requiere un número mínimo de 4 puntos (N >= 4).");
        }
        if (x1 == null || x2 == null || y == null || x1.length != n || x2.length != n || y.length != n) {
            throw new IllegalArgumentException("Los vectores de entrada X1, X2 y Y deben ser no nulos y tener la misma dimensión.");
        }

        List<String> pasos = new ArrayList<>();
        
        // 1. Cálculo de sumatorias
        double sumX1 = 0.0, sumX2 = 0.0, sumY = 0.0;
        double sumX1Sq = 0.0, sumX2Sq = 0.0, sumX1X2 = 0.0;
        double sumX1Y = 0.0, sumX2Y = 0.0;

        for (int i = 0; i < n; i++) {
            sumX1 += x1[i];
            sumX2 += x2[i];
            sumY += y[i];
            sumX1Sq += x1[i] * x1[i];
            sumX2Sq += x2[i] * x2[i];
            sumX1X2 += x1[i] * x2[i];
            sumX1Y += x1[i] * y[i];
            sumX2Y += x2[i] * y[i];
        }

        pasos.add("Paso 1: Cálculo acumulativo de sumatorias:");
        pasos.add(String.format(Locale.US, "ΣX1 = %.4f | ΣX2 = %.4f | ΣY = %.4f", sumX1, sumX2, sumY));
        pasos.add(String.format(Locale.US, "ΣX1² = %.4f | ΣX2² = %.4f | ΣX1X2 = %.4f", sumX1Sq, sumX2Sq, sumX1X2));
        pasos.add(String.format(Locale.US, "ΣX1Y = %.4f | ΣX2Y = %.4f", sumX1Y, sumX2Y));

        // 2. Planteamiento y solución del Sistema 3x3 por Gauss-Jordan
        double[][] sistemaAumentado = {
            {n, sumX1, sumX2, sumY},
            {sumX1, sumX1Sq, sumX1X2, sumX1Y},
            {sumX2, sumX1X2, sumX2Sq, sumX2Y}
        };

        double[] coeficientes = resolverGaussJordan3x3(sistemaAumentado, pasos);
        double a0 = coeficientes[0];
        double a1 = coeficientes[1];
        double a2 = coeficientes[2];

        // 3. Evaluación de estadísticos (St, Sr, Sy/x1x2, R2, R)
        double promedioY = sumY / n;
        double st = 0.0;
        double sr = 0.0;

        for (int i = 0; i < n; i++) {
            double yEstimado = a0 + (a1 * x1[i]) + (a2 * x2[i]);
            st += Math.pow(y[i] - promedioY, 2);
            sr += Math.pow(y[i] - yEstimado, 2);
        }

        double errorEstandar = (n > 3) ? Math.sqrt(sr / (n - 3)) : 0.0;
        double r2 = (st > 0) ? (st - sr) / st : 0.0;
        if (r2 < 0) r2 = 0.0; // Control de desbordamiento numérico
        double r = Math.sqrt(r2);

        pasos.add("Paso 3: Evaluación de errores y coeficientes de correlación:");
        pasos.add(String.format(Locale.US, "Suma Total de Cuadrados (St) = %.6f", st));
        pasos.add(String.format(Locale.US, "Suma de Residuos al Cuadrado (Sr) = %.6f", sr));
        pasos.add(String.format(Locale.US, "Error estándar de la estimación Sy/x1x2 = %.6f", errorEstandar));
        pasos.add(String.format(Locale.US, "Coeficiente de Determinación R² = %.6f (%.2f%%)", r2, r2 * 100));
        pasos.add(String.format(Locale.US, "Coeficiente de Correlación R = %.6f", r));

        // Construcción de la ecuación formal
        String ecuacion = String.format(Locale.US, "y = %.4f %s %.4fx1 %s %.4fx2",
                a0,
                (a1 >= 0 ? "+" : "-"), Math.abs(a1),
                (a2 >= 0 ? "+" : "-"), Math.abs(a2));

        return RegresionMultipleRespuesta.builder()
                .numeroPuntos(n)
                .a0(a0)
                .a1(a1)
                .a2(a2)
                .sumatoriaX1(sumX1)
                .sumatoriaX2(sumX2)
                .sumatoriaY(sumY)
                .sumatoriaX1Cuadrado(sumX1Sq)
                .sumatoriaX2Cuadrado(sumX2Sq)
                .sumatoriaX1X2(sumX1X2)
                .sumatoriaX1Y(sumX1Y)
                .sumatoriaX2Y(sumX2Y)
                .coeficienteDeterminacionR2(r2)
                .coeficienteCorrelacionR(r)
                .error(errorEstandar)
                .ecuacionResultante(ecuacion)
                .pasosDesarrollo(pasos)
                .build();
    }

    private double[] resolverGaussJordan3x3(double[][] m, List<String> pasos) {
        int n = 3;
        pasos.add("Paso 2: Planteamiento de la Matriz Aumentada (3x4):");
        
        for (int i = 0; i < n; i++) {
            int max = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(m[k][i]) > Math.abs(m[max][i])) max = k;
            }
            if (max != i) {
                double[] temp = m[i];
                m[i] = m[max];
                m[max] = temp;
                pasos.add(String.format(Locale.US, "Pivoteo: Intercambio Fila %d <-> Fila %d", i + 1, max + 1));
            }

            double pivote = m[i][i];
            if (Math.abs(pivote) < 1e-12) {
                throw new ArithmeticException("Sistema colineal o singular: Pivote nulo detectado en el método de Gauss-Jordan.");
            }

            for (int j = i; j <= n; j++) {
                m[i][j] /= pivote;
            }

            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = m[k][i];
                    for (int j = i; j <= n; j++) {
                        m[k][j] -= factor * m[i][j];
                    }
                }
            }
        }

        pasos.add(String.format(Locale.US, "Coeficientes calculados: a0 = %.6f, a1 = %.6f, a2 = %.6f",
                m[0][3], m[1][3], m[2][3]));

        return new double[]{m[0][3], m[1][3], m[2][3]};
    }
}