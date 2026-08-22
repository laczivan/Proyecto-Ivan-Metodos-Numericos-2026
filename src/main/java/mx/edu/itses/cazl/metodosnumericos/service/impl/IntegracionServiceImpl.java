package mx.edu.itses.cazl.metodosnumericos.service.impl;

import mx.edu.itses.cazl.metodosnumericos.dto.request.integracion.IntegracionMultipleRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.integracion.Simpson13Request;
import mx.edu.itses.cazl.metodosnumericos.dto.request.integracion.Simpson38Request;
import mx.edu.itses.cazl.metodosnumericos.dto.request.integracion.TrapecioRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.response.integracion.IntegracionMultipleRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.integracion.Simpson13Respuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.integracion.Simpson38Respuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.integracion.TrapecioRespuesta;
import mx.edu.itses.cazl.metodosnumericos.service.IntegracionService;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IntegracionServiceImpl implements IntegracionService {

    @Override
    public TrapecioRespuesta calcularTrapecio(TrapecioRequest request) {
        if (request.getB() <= request.getA()) {
            throw new IllegalArgumentException("El límite superior (b) debe ser mayor al límite inferior (a).");
        }
        if (request.getN() < 1) {
            throw new IllegalArgumentException("El número de segmentos (n) debe ser mayor o igual a 1.");
        }

        List<String> pasos = new ArrayList<>();
        List<TrapecioRespuesta.PuntoEvaluado> tablaPuntos = new ArrayList<>();
        ExprEvaluator evaluator = new ExprEvaluator();

        double a = request.getA();
        double b = request.getB();
        int n = request.getN();
        String fx = request.getFx();

        // 1. Ancho de subintervalo
        double h = (b - a) / n;
        pasos.add(String.format("Paso 1: Cálculo del ancho de subintervalo h = (b - a) / n = (%.4f - %.4f) / %d = %.4f", b, a, n, h));

        // 2. Evaluación de puntos xi y f(xi)
        double sumaIntermedios = 0.0;
        pasos.add("Paso 2: Evaluación de la función f(x) en cada nodo xi:");

        for (int i = 0; i <= n; i++) {
            double xi = a + (i * h);
            
            // Evaluación con Symja
            // Evaluación con Symja usando INum
        String exprString = fx.replaceAll("(?i)x", "(" + xi + ")");
        IExpr result = evaluator.eval("N(" + exprString + ")");
        double fxi = ((INum) result).doubleValue();

            tablaPuntos.add(new TrapecioRespuesta.PuntoEvaluado(i, xi, fxi));
            pasos.add(String.format(" - x_%d = %.4f -> f(x_%d) = %.6f", i, xi, i, fxi));

            if (i > 0 && i < n) {
                sumaIntermedios += fxi;
            }
        }

        // 3. Fórmula de Regla del Trapecio (Simple o Compuesta)
        double f0 = tablaPuntos.get(0).getFxi();
        double fn = tablaPuntos.get(n).getFxi();
        double integralAproximada = (h / 2.0) * (f0 + (2.0 * sumaIntermedios) + fn);

        pasos.add(String.format("Paso 3: Aplicación de la regla del trapecio: I ≈ (h / 2) * [f(x0) + 2*Σf(xi) + f(xn)]"));
        pasos.add(String.format("Integral aproximada obtenida: %.6f", integralAproximada));

        // 4. Integral Exacta con Symja
        double integralExacta = 0.0;
        try {
    String symjaIntegrate = String.format("N(Integrate(%s, {x, %f, %f}))", fx, a, b);
    IExpr exactResult = evaluator.eval(symjaIntegrate);
    integralExacta = ((INum) exactResult).doubleValue();
    pasos.add(String.format("Paso 4: Integral exacta evaluada simbólicamente: %.6f", integralExacta));
    } catch (Exception e) {
    integralExacta = integralAproximada;
    pasos.add("Paso 4: No se pudo calcular la integral exacta simbólicamente.");
    }

        // 5. Error relativo porcentual
        double errorRelativo = 0.0;
        if (integralExacta != 0.0) {
            errorRelativo = Math.abs((integralExacta - integralAproximada) / integralExacta) * 100.0;
        }
        pasos.add(String.format("Paso 5: Cálculo del error relativo porcentual: ER = |(I_exacta - I_aprox) / I_exacta| * 100 = %.4f%%", errorRelativo));

        return new TrapecioRespuesta(
                fx, a, b, n, h, integralAproximada, integralExacta, tablaPuntos, errorRelativo, pasos
        );
    }
    @Override
    public Simpson13Respuesta calcularSimpson13(Simpson13Request request) {
        if (request.getA() >= request.getB()) {
            throw new IllegalArgumentException("El límite superior 'b' debe ser mayor al límite inferior 'a'.");
        }
        if (request.getN() < 2 || request.getN() % 2 != 0) {
            throw new IllegalArgumentException("El número de segmentos 'n' debe ser un número par mayor o igual a 2.");
        }

        Simpson13Respuesta respuesta = new Simpson13Respuesta();
        ExprEvaluator util = new ExprEvaluator();
        List<Map<String, Double>> tablaPuntos = new ArrayList<>();
        List<String> pasos = new ArrayList<>();

        double a = request.getA();
        double b = request.getB();
        int n = request.getN();
        String fx = request.getFx().toLowerCase(); 
        
        // 1. Calcular h
        double h = (b - a) / n;
        pasos.add("Calculando tamaño de paso h = (b - a) / n = (" + b + " - " + a + ") / " + n + " = " + h);

        // 2. Variables para sumatorias
        double sumaImpares = 0.0;
        double sumaPares = 0.0;
        
        // Evaluar f(x0) y f(xn)
        double fa = evaluarFuncion(util, fx, a);
        double fb = evaluarFuncion(util, fx, b);
        
        tablaPuntos.add(Map.of("i", 0.0, "x", a, "fx", fa));

        // 3. Iteraciones
        pasos.add("Iniciando sumatorias para términos pares e impares...");
        for (int i = 1; i < n; i++) {
            double xi = a + (i * h);
            double fxi = evaluarFuncion(util, fx, xi);
            tablaPuntos.add(Map.of("i", (double)i, "x", xi, "fx", fxi));

            if (i % 2 == 0) {
                sumaPares += fxi;
            } else {
                sumaImpares += fxi;
            }
        }
        tablaPuntos.add(Map.of("i", (double)n, "x", b, "fx", fb));

        pasos.add("Suma de índices impares: " + sumaImpares);
        pasos.add("Suma de índices pares: " + sumaPares);

        // 4. Integral Aproximada (Simpson 1/3)
        double integralAprox = (h / 3) * (fa + (4 * sumaImpares) + (2 * sumaPares) + fb);
        pasos.add("Aplicando fórmula: I = (h/3) * [f(x0) + 4∑f(xi_impares) + 2∑f(xj_pares) + f(xn)]");
        pasos.add("I = (" + h + "/3) * [" + fa + " + 4(" + sumaImpares + ") + 2(" + sumaPares + ") + " + fb + "] = " + integralAprox);

        // 5. Integral Exacta con Symja
        String integralSimbolica = "NIntegrate(" + fx + ", {x, " + a + ", " + b + "})";
        double integralExacta = util.evalf(integralSimbolica);
        pasos.add("Integral exacta evaluada con Symja: " + integralExacta);

        // 6. Error Relativo
        double error = Math.abs((integralExacta - integralAprox) / integralExacta) * 100;

        // Asignar al DTO
        respuesta.setFx(request.getFx());
        respuesta.setA(a);
        respuesta.setB(b);
        respuesta.setN(n);
        respuesta.setH(h);
        respuesta.setIntegralAproximada(integralAprox);
        respuesta.setIntegralExacta(integralExacta);
        respuesta.setErrorRelativo(error);
        respuesta.setTablaPuntos(tablaPuntos);
        respuesta.setPasosDesarrollo(pasos);

        return respuesta;
    }

    // Método auxiliar para evaluar funciones
    private double evaluarFuncion(ExprEvaluator util, String funcion, double x) {
        String expr = funcion.replace("x", "(" + x + ")");
        return util.evalf(expr);
    }
    @Override
    public Simpson38Respuesta calcularSimpson38(Simpson38Request request) {
        if (request.getA() >= request.getB()) {
            throw new IllegalArgumentException("El límite superior 'b' debe ser mayor al límite inferior 'a'.");
        }

        Simpson38Respuesta respuesta = new Simpson38Respuesta();
        ExprEvaluator util = new ExprEvaluator();
        List<Map<String, Double>> tablaPuntos = new ArrayList<>();
        List<String> pasos = new ArrayList<>();

        double a = request.getA();
        double b = request.getB();
        String fx = request.getFx().toLowerCase();

        // 1. Calcular h (En Simpson 3/8 simple, n siempre es 3)
        double h = (b - a) / 3.0;
        pasos.add("Para Simpson 3/8 de aplicación simple, el número de segmentos n = 3.");
        pasos.add("Calculando tamaño de paso h = (b - a) / 3 = (" + b + " - " + a + ") / 3 = " + h);

        // 2. Calcular los 4 puntos: x0, x1, x2, x3
        double[] x = { a, a + h, a + 2 * h, b };
        double[] fxi = new double[4];
        
        pasos.add("Evaluando la función en los 4 puntos (x0 a x3):");
        for (int i = 0; i <= 3; i++) {
            fxi[i] = evaluarFuncion(util, fx, x[i]);
            tablaPuntos.add(Map.of("i", (double)i, "x", x[i], "fx", fxi[i]));
            pasos.add("f(x" + i + ") = f(" + x[i] + ") = " + fxi[i]);
        }

        // 3. Integral Aproximada (Fórmula Simpson 3/8 Simple)
        double integralAprox = (3.0 * h / 8.0) * (fxi[0] + 3 * fxi[1] + 3 * fxi[2] + fxi[3]);
        pasos.add("Aplicando la fórmula: I = (3h / 8) * [f(x0) + 3f(x1) + 3f(x2) + f(x3)]");
        pasos.add("I = (" + (3*h/8) + ") * [" + fxi[0] + " + 3(" + fxi[1] + ") + 3(" + fxi[2] + ") + " + fxi[3] + "] = " + integralAprox);

        // 4. Integral Exacta con Symja
        String integralSimbolica = "NIntegrate(" + fx + ", {x, " + a + ", " + b + "})";
        double integralExacta = util.evalf(integralSimbolica);
        pasos.add("Integral exacta evaluada con Symja: " + integralExacta);

        // 5. Error Relativo
        double error = Math.abs((integralExacta - integralAprox) / integralExacta) * 100;

        // 6. Asignar al DTO
        respuesta.setFx(request.getFx());
        respuesta.setA(a);
        respuesta.setB(b);
        respuesta.setH(h);
        respuesta.setIntegralAproximada(integralAprox);
        respuesta.setIntegralExacta(integralExacta);
        respuesta.setErrorRelativo(error);
        respuesta.setTablaPuntos(tablaPuntos);
        respuesta.setPasosDesarrollo(pasos);

        return respuesta;
    }
    @Override
    public IntegracionMultipleRespuesta calcularMultiple(IntegracionMultipleRequest req) {
        if (req.getAx() >= req.getBx() || req.getAy() >= req.getBy()) {
            throw new IllegalArgumentException("Los límites superiores deben ser mayores a los inferiores.");
        }
        if (req.getNx() < 1 || req.getNy() < 1) {
            throw new IllegalArgumentException("El número de segmentos debe ser al menos 1.");
        }

        IntegracionMultipleRespuesta res = new IntegracionMultipleRespuesta();
        ExprEvaluator util = new ExprEvaluator();
        List<String> pasos = new ArrayList<>();

        double hx = (req.getBx() - req.getAx()) / req.getNx();
        double hy = (req.getBy() - req.getAy()) / req.getNy();
        pasos.add("hx = " + hx + ", hy = " + hy);

        double suma = 0.0;
        pasos.add("Iniciando iteración sobre la cuadrícula...");
        
        for (int i = 0; i <= req.getNx(); i++) {
            for (int j = 0; j <= req.getNy(); j++) {
                double xi = req.getAx() + i * hx;
                double yj = req.getAy() + j * hy;
                
                String expr = req.getFxy().replace("x", "(" + xi + ")").replace("y", "(" + yj + ")");
                double fval = util.evalf(expr);
                
                int peso = 1;
                if (i > 0 && i < req.getNx()) peso *= 2;
                if (j > 0 && j < req.getNy()) peso *= 2;
                
                suma += peso * fval;
            }
        }

        double integralAprox = (hx * hy / 4.0) * suma;
        pasos.add("Suma ponderada total: " + suma);
        pasos.add("Integral Aproximada = (hx * hy / 4) * Suma = " + integralAprox);

        String exprSymja = "NIntegrate(" + req.getFxy() + ", {x, " + req.getAx() + ", " + req.getBx() + "}, {y, " + req.getAy() + ", " + req.getBy() + "})";
        double integralExacta = util.evalf(exprSymja);
        pasos.add("Integral Exacta (Symja): " + integralExacta);

        double error = Math.abs((integralExacta - integralAprox) / integralExacta) * 100;

        res.setFxy(req.getFxy());
        res.setHx(hx); res.setHy(hy);
        res.setIntegralAproximada(integralAprox);
        res.setIntegralExacta(integralExacta);
        res.setErrorRelativo(error);
        res.setPasosDesarrollo(pasos);

        return res;
    }
}