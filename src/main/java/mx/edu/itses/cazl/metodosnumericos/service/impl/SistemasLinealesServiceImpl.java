package mx.edu.itses.cazl.metodosnumericos.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.itses.cazl.metodosnumericos.dto.request.sistemaslineales.DeterminanteRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.sistemaslineales.EliminacionGaussianaRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.sistemaslineales.GaussJordanRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.sistemaslineales.GaussSeidelRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.sistemaslineales.JacobiRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales.DeterminanteRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales.EliminacionGaussianaRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales.GaussJordanRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales.GaussSeidelIteracion;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales.GaussSeidelRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales.JacobiIteracionRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales.JacobiRespuesta;
import mx.edu.itses.cazl.metodosnumericos.service.SistemasLinealesService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class SistemasLinealesServiceImpl implements SistemasLinealesService {

    @Override
    public DeterminanteRespuesta calcularDeterminante(DeterminanteRequest request) {
        int n = request.getDimension();
        double[][] a = request.getMatriz();

        // 10.6 Validación Fail-Fast de dimensión
        if (n < 2 || n > 4) {
            log.error("Dimensión fuera de rango especificado: {}", n);
            throw new IllegalArgumentException("El sistema sólo admite matrices de dimensiones 2x2, 3x3 y 4x4.");
        }

        if (a == null || a.length != n) {
            throw new IllegalArgumentException("La estructura de la matriz no coincide con la dimensión especificada.");
        }

        // Copia profunda de la matriz original
        double[][] matrizOriginal = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, matrizOriginal[i], 0, n);
        }

        List<String> pasos = new ArrayList<>();
        double determinante;
        String metodo;

        switch (n) {
            case 2 -> {
                metodo = "Regla Directa (Fórmula 2x2)";
                determinante = calcularDeterminante2x2(a, pasos);
            }
            case 3 -> {
                metodo = "Expansión por Cofactores (Laplace)";
                determinante = calcularDeterminante3x3(a, pasos);
            }
            case 4 -> {
                metodo = "Eliminación Gaussiana con Pivoteo Parcial";
                determinante = calcularDeterminante4x4Gauss(a, pasos);
            }
            default -> throw new IllegalStateException("Dimensión inesperada: " + n);
        }

        log.info("Determinante calculado con éxito para {}x{}: {}", n, n, determinante);

        return DeterminanteRespuesta.builder()
                .dimension(n)
                .matrizOriginal(matrizOriginal)
                .determinante(determinante)
                .metodoUtilizado(metodo)
                .pasosDesarrollo(pasos)
                .build();
    }

    private double calcularDeterminante2x2(double[][] m, List<String> pasos) {
        double det = (m[0][0] * m[1][1]) - (m[0][1] * m[1][0]);
        pasos.add(String.format(Locale.US, "Fórmula directa: det(A) = (a0,0 * a1,1) - (a0,1 * a1,0)"));
        pasos.add(String.format(Locale.US, "det(A) = (%.4f * %.4f) - (%.4f * %.4f)", m[0][0], m[1][1], m[0][1], m[1][0]));
        pasos.add(String.format(Locale.US, "det(A) = %.4f", det));
        return det;
    }

    private double calcularDeterminante3x3(double[][] m, List<String> pasos) {
        pasos.add("Expansión por cofactores a lo largo de la primera fila:");

        // Menor M0,0
        double m00 = (m[1][1] * m[2][2]) - (m[1][2] * m[2][1]);
        pasos.add(String.format(Locale.US, "Menor M0,0 = (%.4f * %.4f) - (%.4f * %.4f) = %.4f",
                m[1][1], m[2][2], m[1][2], m[2][1], m00));

        // Menor M0,1
        double m01 = (m[1][0] * m[2][2]) - (m[1][2] * m[2][0]);
        pasos.add(String.format(Locale.US, "Menor M0,1 = (%.4f * %.4f) - (%.4f * %.4f) = %.4f",
                m[1][0], m[2][2], m[1][2], m[2][0], m01));

        // Menor M0,2
        double m02 = (m[1][0] * m[2][1]) - (m[1][1] * m[2][0]);
        pasos.add(String.format(Locale.US, "Menor M0,2 = (%.4f * %.4f) - (%.4f * %.4f) = %.4f",
                m[1][0], m[2][1], m[1][1], m[2][0], m02));

        double det = (m[0][0] * m00) - (m[0][1] * m01) + (m[0][2] * m02);
        pasos.add(String.format(Locale.US, "det(A) = a0,0*M0,0 - a0,1*M0,1 + a0,2*M0,2"));
        pasos.add(String.format(Locale.US, "det(A) = (%.4f * %.4f) - (%.4f * %.4f) + (%.4f * %.4f)",
                m[0][0], m00, m[0][1], m01, m[0][2], m02));
        pasos.add(String.format(Locale.US, "det(A) = %.4f", det));

        return det;
    }

    private double calcularDeterminante4x4Gauss(double[][] m, List<String> pasos) {
        int n = 4;
        double[][] a = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(m[i], 0, a[i], 0, n);
        }

        pasos.add("Transformación a Matriz Triangular Superior utilizando Eliminación Gaussiana con Pivoteo Parcial:");
        int permutacionesFila = 0;

        for (int i = 0; i < n; i++) {
            // Pivoteo Parcial
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
                permutacionesFila++;
                pasos.add(String.format(Locale.US, "Intercambio de Renglón %d <-> Renglón %d (Inversión de signo en determinante)", i + 1, maxFila + 1));
            }

            if (Math.abs(a[i][i]) < 1e-12) {
                pasos.add(String.format(Locale.US, "Pivote cero encontrado en la columna %d. El determinante es 0.0000", i + 1));
                return 0.0;
            }

            // Eliminación de las filas inferiores
            for (int j = i + 1; j < n; j++) {
                double factor = a[j][i] / a[i][i];
                pasos.add(String.format(Locale.US, "Fila %d = Fila %d - (%.4f) * Fila %d", j + 1, j + 1, factor, i + 1));
                for (int k = i; k < n; k++) {
                    a[j][k] -= factor * a[i][k];
                }
            }
        }

        // Producto de la diagonal principal
        double det = 1.0;
        StringBuilder diagExpr = new StringBuilder();
        for (int i = 0; i < n; i++) {
            det *= a[i][i];
            diagExpr.append(String.format(Locale.US, "%.4f", a[i][i]));
            if (i < n - 1) diagExpr.append(" * ");
        }

        pasos.add(String.format(Locale.US, "Producto Diagonal Principal: %s = %.4f", diagExpr.toString(), det));

        if (permutacionesFila % 2 != 0) {
            det = -det;
            pasos.add(String.format(Locale.US, "Aplicando ajuste por número impar de intercambios (%d): det(A) = %.4f", permutacionesFila, det));
        } else {
            pasos.add(String.format(Locale.US, "Ajuste por permutaciones par (%d intercambios): Signo conservado", permutacionesFila));
        }

        return det;
    }
    @Override
    public EliminacionGaussianaRespuesta eliminacionGaussiana(EliminacionGaussianaRequest request) {
        int n = request.getDimension();
        double[][] a = request.getMatrizA();
        double[] b = request.getVectorB();

        // 1. Validación Fail-Fast
        if (n < 2 || n > 4) {
            throw new IllegalArgumentException("El sistema sólo admite dimensiones cuadradas de 2x2, 3x3 y 4x4.");
        }
        if (a == null || a.length != n || b == null || b.length != n) {
            throw new IllegalArgumentException("La estructura de la matriz A o del vector B es incoherente.");
        }

        // Resguardo de datos originales
        double[][] aOriginal = new double[n][n];
        double[] bOriginal = new double[n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, aOriginal[i], 0, n);
            bOriginal[i] = b[i];
        }

        // Construcción de la Matriz Aumentada [A|B]
        double[][] aumentada = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, aumentada[i], 0, n);
            aumentada[i][n] = b[i];
        }

        List<String> pasos = new ArrayList<>();
        pasos.add("Matriz Aumentada Inicial [A|B]:");
        pasos.add(formatearMatrizAumentada(aumentada, n));

        // ETAPA 1: Triangularización Hacia Adelante con Pivoteo Parcial
        for (int k = 0; k < n - 1; k++) {
            pasos.add(String.format("=== ETAPA %d: Pivoteo y Eliminación en Columna %d ===", k + 1, k + 1));

            // Pivoteo parcial
            int maxFila = k;
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(aumentada[i][k]) > Math.abs(aumentada[maxFila][k])) {
                    maxFila = i;
                }
            }

            if (maxFila != k) {
                double[] temp = aumentada[k];
                aumentada[k] = aumentada[maxFila];
                aumentada[maxFila] = temp;
                pasos.add(String.format("Pivoteo Parcial: Intercambio de Fila %d <-> Fila %d", k + 1, maxFila + 1));
                pasos.add(formatearMatrizAumentada(aumentada, n));
            }

            double pivote = aumentada[k][k];
            if (Math.abs(pivote) < 1e-12) {
                throw new ArithmeticException(String.format("Pivote cero encontrado en A[%d][%d]. El sistema no tiene solución única.", k + 1, k + 1));
            }

            // Eliminación
            for (int i = k + 1; i < n; i++) {
                double factor = aumentada[i][k] / pivote;
                pasos.add(String.format(Locale.US, "Factor m(%d,%d) = %.4f / %.4f = %.4f", i + 1, k + 1, aumentada[i][k], pivote, factor));
                pasos.add(String.format(Locale.US, "Fila %d = Fila %d - (%.4f) * Fila %d", i + 1, i + 1, factor, k + 1));

                for (int j = k; j <= n; j++) {
                    aumentada[i][j] -= factor * aumentada[k][j];
                }
            }
            pasos.add(formatearMatrizAumentada(aumentada, n));
        }

        // Extracción de la matriz triangular superior
        double[][] aTriangular = new double[n][n];
        double[] bTriangular = new double[n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(aumentada[i], 0, aTriangular[i], 0, n);
            bTriangular[i] = aumentada[i][n];
        }

        // ETAPA 2: Sustitución Hacia Atrás
        pasos.add("=== ETAPA 2: Sustitución Hacia Atrás ===");
        double[] x = new double[n];

        // Despeje de la última incógnita (x_n)
        if (Math.abs(aumentada[n - 1][n - 1]) < 1e-12) {
            throw new ArithmeticException("Sistema singular en la sustitución hacia atrás.");
        }
        x[n - 1] = aumentada[n - 1][n] / aumentada[n - 1][n - 1];
        pasos.add(String.format(Locale.US, "x[%d] = %.4f / %.4f = %.4f", n, aumentada[n - 1][n], aumentada[n - 1][n - 1], x[n - 1]));

        // Despeje hacia atrás para el resto de incógnitas
        for (int i = n - 2; i >= 0; i--) {
            double suma = 0.0;
            StringBuilder exprSuma = new StringBuilder();

            for (int j = i + 1; j < n; j++) {
                double termino = aumentada[i][j] * x[j];
                suma += termino;
                exprSuma.append(String.format(Locale.US, " + (%.4f * %.4f)", aumentada[i][j], x[j]));
            }

            x[i] = (aumentada[i][n] - suma) / aumentada[i][i];
            pasos.add(String.format(Locale.US, "x[%d] = (%.4f - (%s)) / %.4f = %.4f",
                    i + 1, aumentada[i][n], exprSuma.length() > 3 ? exprSuma.substring(3) : "0", aumentada[i][i], x[i]));
        }

        log.info("Eliminación Gaussiana completada exitosamente para sistema {}x{}", n, n);

        return EliminacionGaussianaRespuesta.builder()
                .dimension(n)
                .matrizAOriginal(aOriginal)
                .vectorBOriginal(bOriginal)
                .matrizATriangular(aTriangular)
                .vectorBTriangular(bTriangular)
                .vectorSolucionX(x)
                .metodoUtilizado("Eliminación Gaussiana con Pivoteo Parcial y Sustitución Hacia Atrás")
                .pasosDesarrollo(pasos)
                .build();
    }

    @Override
    public GaussJordanRespuesta gaussJordan(GaussJordanRequest request) {
        int n = request.getDimension();
        double[][] a = request.getMatrizA();
        double[] b = request.getVectorB();

        // 1. Validación Fail-Fast
        if (n < 2 || n > 4) {
            throw new IllegalArgumentException("El método de Gauss-Jordan admite únicamente sistemas de 2x2, 3x3 y 4x4.");
        }
        if (a == null || a.length != n || b == null || b.length != n) {
            throw new IllegalArgumentException("La matriz A o el vector B no coinciden con la dimensión especificada.");
        }

        // Copia profunda para preservar los datos de entrada
        double[][] aOriginal = new double[n][n];
        double[] bOriginal = new double[n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, aOriginal[i], 0, n);
            bOriginal[i] = b[i];
        }

        // Matriz Aumentada [A|B]
        double[][] aumentada = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, aumentada[i], 0, n);
            aumentada[i][n] = b[i];
        }

        List<String> pasos = new ArrayList<>();
        pasos.add("Matriz Aumentada Inicial [A|B]:");
        pasos.add(formatearMatrizAumentada(aumentada, n));

        // 2. Algoritmo de Reducción Escalonada Completa (Gauss-Jordan)
        for (int k = 0; k < n; k++) {
            pasos.add(String.format("=== ETAPA %d: Normalización y Eliminación Total en Columna %d ===", k + 1, k + 1));

            // Pivoteo Parcial
            int maxFila = k;
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(aumentada[i][k]) > Math.abs(aumentada[maxFila][k])) {
                    maxFila = i;
                }
            }

            if (maxFila != k) {
                double[] temp = aumentada[k];
                aumentada[k] = aumentada[maxFila];
                aumentada[maxFila] = temp;
                pasos.add(String.format("Pivoteo Parcial: Intercambio Renglón %d <-> Renglón %d", k + 1, maxFila + 1));
                pasos.add(formatearMatrizAumentada(aumentada, n));
            }

            double pivote = aumentada[k][k];
            if (Math.abs(pivote) < 1e-12) {
                throw new ArithmeticException(String.format("Pivote nulo o cercano a cero en A[%d][%d]. El sistema no tiene solución única.", k + 1, k + 1));
            }

            // Normalizar el renglón pivote para que A[k][k] = 1.0
            pasos.add(String.format(Locale.US, "Normalizando Renglón %d: R%d = R%d / %.4f", k + 1, k + 1, k + 1, pivote));
            for (int j = k; j <= n; j++) {
                aumentada[k][j] /= pivote;
            }
            pasos.add(formatearMatrizAumentada(aumentada, n));

            // Eliminación simultánea en filas superiores e inferiores
            for (int i = 0; i < n; i++) {
                if (i != k) {
                    double factor = aumentada[i][k];
                    if (Math.abs(factor) > 1e-12) {
                        pasos.add(String.format(Locale.US, "Anulando elemento R%d[%d]: R%d = R%d - (%.4f) * R%d", i + 1, k + 1, i + 1, i + 1, factor, k + 1));
                        for (int j = k; j <= n; j++) {
                            aumentada[i][j] -= factor * aumentada[k][j];
                        }
                    }
                }
            }
            pasos.add(formatearMatrizAumentada(aumentada, n));
        }

        // Extracción de la Matriz Identidad y Vector Solución Directo
        double[][] matrizIdentidad = new double[n][n];
        double[] vectorSolucionX = new double[n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(aumentada[i], 0, matrizIdentidad[i], 0, n);
            vectorSolucionX[i] = aumentada[i][n];
        }

        log.info("Gauss-Jordan procesado exitosamente para dimensión {}x{}", n, n);

        return GaussJordanRespuesta.builder()
                .dimension(n)
                .matrizAOriginal(aOriginal)
                .vectorBOriginal(bOriginal)
                .matrizIdentidad(matrizIdentidad)
                .vectorSolucionX(vectorSolucionX)
                .metodoUtilizado("Método de Gauss-Jordan (Matriz Escalonada Reducida por Renglones)")
                .pasosDesarrollo(pasos)
                .build();
    }

    private String formatearMatrizAumentada(double[][] m, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("| ");
            for (int j = 0; j < n; j++) {
                sb.append(String.format(Locale.US, "%8.4f ", m[i][j]));
            }
            sb.append(String.format(Locale.US, "| %8.4f |%n", m[i][n]));
        }
        return sb.toString();

        
    }
    @Override
    public JacobiRespuesta jacobi(JacobiRequest request) {
        int n = request.getDimension();
        double[][] a = request.getMatrizA();
        double[] b = request.getVectorB();

        // 1. Validación Fail-Fast de dimensión
        if (n < 2 || n > 4) {
            throw new IllegalArgumentException("El método de Jacobi admite únicamente dimensiones de 2x2, 3x3 y 4x4.");
        }
        if (a == null || a.length != n || b == null || b.length != n) {
            throw new IllegalArgumentException("La estructura de la matriz A o del vector B es incoherente.");
        }

        // Resguardo de datos originales
        double[][] aOriginal = new double[n][n];
        double[] bOriginal = new double[n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, aOriginal[i], 0, n);
            bOriginal[i] = b[i];
        }

        // 2. Validación de Ceros en la Diagonal Principal
        for (int i = 0; i < n; i++) {
            if (Math.abs(a[i][i]) < 1e-12) {
                throw new ArithmeticException(
                        String.format(Locale.US, "El elemento A[%d][%d] en la diagonal principal es cero. El método de Jacobi no puede dividir por cero.", i + 1, i + 1)
                );
            }
        }

        List<String> observaciones = new ArrayList<>();
        boolean esDominante = verificarDominanciaDiagonal(a, n, observaciones);

        // Vector Inicial X0
        double[] x0 = new double[n];
        if (request.getVectorX0() != null && request.getVectorX0().length == n) {
            System.arraycopy(request.getVectorX0(), 0, x0, 0, n);
        }

        double[] xAnterior = new double[n];
        System.arraycopy(x0, 0, xAnterior, 0, n);

        List<JacobiIteracionRespuesta> iteraciones = new ArrayList<>();
        boolean convergio = false;
        double[] xNuevo = new double[n];

        // 3. Algoritmo Iterativo de Jacobi
        for (int k = 1; k <= request.getMaximoIteraciones(); k++) {
            xNuevo = new double[n];
            double errorMaximo = 0.0;

            for (int i = 0; i < n; i++) {
                double suma = 0.0;
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        suma += a[i][j] * xAnterior[j];
                    }
                }
                xNuevo[i] = (b[i] - suma) / a[i][i];

                // Error relativo porcentual aproximado
                double errorVariable = (xNuevo[i] != 0)
                        ? Math.abs((xNuevo[i] - xAnterior[i]) / xNuevo[i]) * 100.0
                        : Math.abs(xNuevo[i] - xAnterior[i]);

                if (errorVariable > errorMaximo) {
                    errorMaximo = errorVariable;
                }
            }

            // Almacenar registro de la iteración
            iteraciones.add(JacobiIteracionRespuesta.builder()
                    .iteracion(k)
                    .vectorX(xNuevo.clone())
                    .errorRelativo(errorMaximo)
                    .build());

            // Criterio de convergencia
            if (errorMaximo < request.getTolerancia()) {
                convergio = true;
                observaciones.add(String.format(Locale.US, "El método convergió exitosamente en la iteración %d con un error de %.6f%%.", k, errorMaximo));
                break;
            }

            xAnterior = xNuevo.clone();
        }

        if (!convergio) {
            observaciones.add(String.format(Locale.US, "Se alcanzó el límite de %d iteraciones sin alcanzar la tolerancia especificada (%.6f%%).",
                    request.getMaximoIteraciones(), request.getTolerancia()));
        }

        log.info("Método de Jacobi finalizado para dimensión {}x{}. Convergió: {}", n, n, convergio);

        return JacobiRespuesta.builder()
                .dimension(n)
                .matrizAOriginal(aOriginal)
                .vectorBOriginal(bOriginal)
                .vectorX0(x0)
                .vectorSolucionX(xNuevo)
                .iteraciones(iteraciones)
                .esDiagonalmenteDominante(esDominante)
                .convergio(convergio)
                .metodoUtilizado("Método Iterativo de Jacobi")
                .observaciones(observaciones)
                .build();
    }

    
    @Override
    public GaussSeidelRespuesta gaussSeidel(GaussSeidelRequest request) {
        int n = request.getDimension();
        double[][] a = request.getMatrizA();
        double[] b = request.getVectorB();

        // 1. Validación Fail-Fast de dimensiones (2x2 a 4x4)
        if (n < 2 || n > 4) {
            throw new IllegalArgumentException("El método de Gauss-Seidel admite únicamente dimensiones de 2x2, 3x3 y 4x4.");
        }
        if (a == null || a.length != n || b == null || b.length != n) {
            throw new IllegalArgumentException("La estructura de la matriz A o del vector B es incoherente.");
        }

        // Resguardo de matrices/vectores originales
        double[][] aOriginal = new double[n][n];
        double[] bOriginal = new double[n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, aOriginal[i], 0, n);
            bOriginal[i] = b[i];
        }

        // 2. Validación de ceros en la diagonal principal
        for (int i = 0; i < n; i++) {
            if (Math.abs(a[i][i]) < 1e-12) {
                throw new ArithmeticException(
                        String.format(Locale.US, "El elemento A[%d][%d] es cero. El método de Gauss-Seidel no puede dividir por cero en la diagonal.", i + 1, i + 1)
                );
            }
        }

        List<String> observaciones = new ArrayList<>();
        boolean esDominante = verificarDominanciaDiagonal(a, n, observaciones);

        // Vector inicial X0
        double[] x0 = new double[n];
        if (request.getVectorX0() != null && request.getVectorX0().length == n) {
            System.arraycopy(request.getVectorX0(), 0, x0, 0, n);
        }

        // Vector de trabajo modificado in-situ (característica distintiva de Gauss-Seidel)
        double[] xActual = x0.clone();
        List<GaussSeidelIteracion> iteraciones = new ArrayList<>();
        boolean convergio = false;

        // 3. Algoritmo Iterativo de Gauss-Seidel
        for (int k = 1; k <= request.getMaximoIteraciones(); k++) {
            double[] xAnterior = xActual.clone();
            double errorMaximo = 0.0;

            for (int i = 0; i < n; i++) {
                double suma = 0.0;
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        // Utiliza los valores ya actualizados en la iteración actual k (para j < i) 
                        // y los valores de la iteración anterior (para j > i)
                        suma += a[i][j] * xActual[j];
                    }
                }
                xActual[i] = (b[i] - suma) / a[i][i];

                // Error relativo porcentual de la componente i
                double errorVariable = (xActual[i] != 0.0)
                        ? Math.abs((xActual[i] - xAnterior[i]) / xActual[i]) * 100.0
                        : Math.abs(xActual[i] - xAnterior[i]);

                if (errorVariable > errorMaximo) {
                    errorMaximo = errorVariable;
                }
            }

            // Registrar paso
            iteraciones.add(GaussSeidelIteracion.builder()
                    .iteracion(k)
                    .valoresX(xActual.clone())
                    .errorRelativo(errorMaximo)
                    .build());

            // Criterio de parada
            if (errorMaximo < request.getTolerancia()) {
                convergio = true;
                observaciones.add(String.format(Locale.US, "El método convergió exitosamente en la iteración %d con un error de %.6f%%.", k, errorMaximo));
                break;
            }
        }

        if (!convergio) {
            observaciones.add(String.format(Locale.US, "Se alcanzó el límite de %d iteraciones sin alcanzar la tolerancia especificada (%.6f%%).",
                    request.getMaximoIteraciones(), request.getTolerancia()));
        }

        log.info("Gauss-Seidel procesado para sistema {}x{}. Convergió: {}", n, n, convergio);

        return GaussSeidelRespuesta.builder()
                .dimension(n)
                .matrizAOriginal(aOriginal)
                .vectorBOriginal(bOriginal)
                .vectorX0(x0)
                .vectorSolucion(xActual)
                .iteraciones(iteraciones)
                .esDiagonalmenteDominante(esDominante)
                .convergio(convergio)
                .metodoUtilizado("Método Iterativo de Gauss-Seidel")
                .observaciones(observaciones)
                .build();
    }

    private boolean verificarDominanciaDiagonal(double[][] a, int n, List<String> observaciones) {
        boolean esDominante = true;
        for (int i = 0; i < n; i++) {
            double sumaFila = 0.0;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    sumaFila += Math.abs(a[i][j]);
                }
            }
            if (Math.abs(a[i][i]) <= sumaFila) {
                esDominante = false;
            }
        }
        if (esDominante) {
            observaciones.add("La matriz ES estrictamente diagonalmente dominante. Se garantiza la convergencia acelerada.");
        } else {
            observaciones.add("Atención: La matriz NO es estrictamente diagonalmente dominante. El método podría divergir o requerir más iteraciones.");
        }
        return esDominante;
    }
}