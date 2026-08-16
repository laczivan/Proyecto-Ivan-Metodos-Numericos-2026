package mx.edu.itses.cazl.metodosnumericos.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para los algoritmos de Ajuste de Curvas (Fases 17 a 21).
 */
@Slf4j
@Controller
@RequestMapping("/ajuste-curvas")
@RequiredArgsConstructor
public class AjusteCurvasController {

    private final AjusteCurvasService ajusteCurvasService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("titulo", "Unidad IV.- Ajuste de Curvas");
        return "views/ajuste-curvas/index";
    }

    // ==========================================
    // FASE 17: DIFERENCIAS DIVIDIDAS DE NEWTON
    // ==========================================
    @GetMapping("/diferencias-divididas")
    public String formDiferenciasDivididas(Model model) {
        model.addAttribute("titulo", "Diferencias Divididas de Newton");
        
        // ¡El cambio está aquí! Ahora se llama "requestDTO"
        model.addAttribute("requestDTO", new DiferenciasDivididasRequest()); 
        
        return "views/ajuste-curvas/diferencias-divididas/form";
    }

    @PostMapping("/diferencias-divididas")
    public String algoritmoDiferenciasDivididas(
            // ¡Y también debes cambiarlo aquí para que reciba los datos correctamente!
            @ModelAttribute("requestDTO") DiferenciasDivididasRequest request, 
            Model model, 
            RedirectAttributes redirectAttributes) {
            
        log.info("Procesando Diferencias Divididas para {} puntos", request.getNumeroPuntos());
        try {
            DiferenciasDivididasRespuesta resultado = ajusteCurvasService.diferenciasDivididas(request);
            model.addAttribute("resultado", resultado);
            model.addAttribute("titulo", "Solución - Diferencias Divididas");
            return "views/ajuste-curvas/diferencias-divididas/solucion";
        } catch (Exception e) {
            log.error("Error en Diferencias Divididas: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/ajuste-curvas/diferencias-divididas";
        }
    }

    // ==========================================
    // FASE 18: POLINOMIO DE LAGRANGE
    // ==========================================
    @GetMapping("/lagrange")
    public String formLagrange(Model model) {
        model.addAttribute("titulo", "Polinomio de Lagrange");
        model.addAttribute("lagrangeDTO", new LagrangeRequest());
        return "views/ajuste-curvas/lagrange/form";
    }

    @PostMapping("/lagrange")
    public String algoritmoLagrange(
            @ModelAttribute("lagrangeDTO") LagrangeRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.info("Procesando Polinomio de Lagrange para {} puntos", request.getNumeroPuntos());
        try {
            LagrangeRespuesta resultado = ajusteCurvasService.lagrange(request);
            model.addAttribute("resultado", resultado);
            model.addAttribute("titulo", "Solución - Lagrange");
            return "views/ajuste-curvas/lagrange/solucion";
        } catch (Exception e) {
            log.error("Error en Lagrange: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/ajuste-curvas/lagrange";
        }
    }

    // ==========================================
    // FASE 19: REGRESIÓN LINEAL
    // ==========================================
    @GetMapping("/regresion-lineal")
    public String formRegresionLineal(
            @RequestParam(value = "puntos", defaultValue = "2") Integer puntos, 
            Model model) {
            
        if (puntos < 2) puntos = 2;

        // Usamos el Builder y Collections.nCopies como sugirió tu código anterior
        RegresionLinealRequest request = RegresionLinealRequest.builder()
                .numeroPuntos(puntos)
                .valoresX(new java.util.ArrayList<>(java.util.Collections.nCopies(puntos, 0.0)))
                .valoresY(new java.util.ArrayList<>(java.util.Collections.nCopies(puntos, 0.0)))
                .build();

        model.addAttribute("titulo", "Regresión Lineal");
        model.addAttribute("regresionRequest", request);
        
        return "views/ajuste-curvas/regresion-lineal/form";
    }

    @PostMapping("/regresion-lineal")
    public String algoritmoRegresionLineal(
            @ModelAttribute("regresionRequest") RegresionLinealRequest request, 
            Model model,
            RedirectAttributes redirectAttributes) {
            
        log.info("Procesando Regresión Lineal para {} puntos", request.getNumeroPuntos());
        try {
            RegresionLinealRespuesta respuesta = ajusteCurvasService.regresionLineal(request);
            
            // ¡Cambio clave! El HTML de solución espera "respuesta", no "resultado"
            model.addAttribute("respuesta", respuesta);
            model.addAttribute("titulo", "Solución - Regresión Lineal");
            
            return "views/ajuste-curvas/regresion-lineal/solucion";
        } catch (Exception e) {
            log.error("Error en Regresión Lineal: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/ajuste-curvas/regresion-lineal";
        }
    }


    // ==========================================
    // FASE 20: REGRESIÓN POLINOMIAL
    // ==========================================
    // ==========================================
    // FASE 20: REGRESIÓN POLINOMIAL
    // ==========================================
    @GetMapping("/regresion-polinomial")
    public String formRegresionPolinomial(
            @RequestParam(value = "puntos", defaultValue = "3") Integer puntos,
            @RequestParam(value = "grado", defaultValue = "2") Integer grado,
            Model model) {
            
        // Validaciones mínimas: polinomial requiere al menos 3 puntos y grado 2
        if (puntos < 3) puntos = 3;
        if (grado < 2) grado = 2;

        // Construimos el objeto con las listas llenas de ceros
        RegresionPolinomialRequest peticion = RegresionPolinomialRequest.builder()
                .numeroPuntos(puntos)
                .grado(grado)
                .valoresX(new java.util.ArrayList<>(java.util.Collections.nCopies(puntos, 0.0)))
                .valoresY(new java.util.ArrayList<>(java.util.Collections.nCopies(puntos, 0.0)))
                .build();

        model.addAttribute("titulo", "Regresión Polinomial");
        // ¡AQUÍ ESTÁ EL CAMBIO! Le ponemos "request" exactamente como lo pide el HTML
        model.addAttribute("request", peticion);
        
        return "views/ajuste-curvas/regresion-polinomial/form";
    }

    @PostMapping("/regresion-polinomial")
    public String algoritmoRegresionPolinomial(
            // ¡Y AQUÍ TAMBIÉN lo recibimos como "request"!
            @ModelAttribute("request") RegresionPolinomialRequest peticion, 
            Model model,
            RedirectAttributes redirectAttributes) {
            
        log.info("Procesando Regresión Polinomial de grado {} para {} puntos", peticion.getGrado(), peticion.getNumeroPuntos());
        try {
            RegresionPolinomialRespuesta resultado = ajusteCurvasService.regresionPolinomial(peticion);
            
            model.addAttribute("resultado", resultado);
            model.addAttribute("titulo", "Solución - Regresión Polinomial");
            
            return "views/ajuste-curvas/regresion-polinomial/solucion";
        } catch (Exception e) {
            log.error("Error en Regresión Polinomial: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/ajuste-curvas/regresion-polinomial";
        }
    }

    

    // ==========================================
    // FASE 21: REGRESIÓN LINEAL MÚLTIPLE
    // ==========================================
    @GetMapping("/regresion-multiple")
    public String formRegresionMultiple(Model model) {
        // Se declara la variable como entero y se inicializa
        int puntos = 4; 
        
        RegresionMultipleRequest dto = RegresionMultipleRequest.builder()
                .numeroPuntos(puntos)
                .valoresX1(new double[puntos])
                .valoresX2(new double[puntos])
                .valoresY(new double[puntos])
                .build();
        
        model.addAttribute("regresionMultipleDTO", dto);
        model.addAttribute("title", "Regresión Lineal Múltiple");
        return "views/ajuste-curvas/regresion-multiple/form";
    }

    @PostMapping("/regresion-multiple")
    public String resolverRegresionMultiple(
            @ModelAttribute("regresionMultipleDTO") RegresionMultipleRequest request, 
            Model model) {
        
        log.info("Procesando formulario de Regresión Lineal Múltiple...");
        try {
            RegresionMultipleRespuesta resultado = ajusteCurvasService.regresionMultiple(request);
            model.addAttribute("resultado", resultado);
            model.addAttribute("title", "Solución - Regresión Lineal Múltiple");
            return "views/ajuste-curvas/regresion-multiple/solucion";
        } catch (Exception e) {
            log.error("Error en Regresión Lineal Múltiple: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("regresionMultipleDTO", request);
            model.addAttribute("title", "Regresión Lineal Múltiple");
            return "views/ajuste-curvas/regresion-multiple/form";
        }
    }
}