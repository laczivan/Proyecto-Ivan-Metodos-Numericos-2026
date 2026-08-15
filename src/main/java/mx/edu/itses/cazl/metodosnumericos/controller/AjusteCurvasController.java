package mx.edu.itses.cazl.metodosnumericos.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.itses.cazl.metodosnumericos.dto.request.ajustecurvas.DiferenciasDivididasRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.ajustecurvas.LagrangeRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.ajustecurvas.RegresionLinealRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.response.ajustecurvas.DiferenciasDivididasRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.ajustecurvas.LagrangeRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.ajustecurvas.RegresionLinealRespuesta;
import mx.edu.itses.cazl.metodosnumericos.service.AjusteCurvasService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;

@Slf4j
@Controller
@RequestMapping("/ajuste-curvas")
@RequiredArgsConstructor
public class AjusteCurvasController {

    private final AjusteCurvasService ajusteCurvasService;

    @GetMapping
    public String index(Model model) {
        // Retorna el index.html que mostraste al principio
        return "views/ajuste-curvas/index"; 
    }
    @GetMapping("/diferencias-divididas")
    public String diferenciasDivididasForm(Model model) {
        DiferenciasDivididasRequest request = new DiferenciasDivididasRequest();
        request.setNumeroPuntos(3); // Valor predeterminado (orden 2)
        request.setValoresX(new ArrayList<>());
        request.setValoresY(new ArrayList<>());

        model.addAttribute("titulo", "Diferencias Divididas de Newton");
        model.addAttribute("requestDTO", request);
        return "views/ajuste-curvas/diferencias-divididas/form";
    }

    @PostMapping("/diferencias-divididas")
    public String diferenciasDivididasCalcular(@ModelAttribute("requestDTO") DiferenciasDivididasRequest request, Model model) {
        log.info("Procesando Diferencias Divididas de Newton para {} puntos", request.getNumeroPuntos());
        try {
            DiferenciasDivididasRespuesta respuesta = ajusteCurvasService.diferenciasDivididas(request);
            model.addAttribute("resultado", respuesta);
            model.addAttribute("titulo", "Solución - Diferencias Divididas de Newton");
            return "views/ajuste-curvas/diferencias-divididas/solucion";
        } catch (IllegalArgumentException e) {
            log.error("Error en Diferencias Divididas: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("titulo", "Diferencias Divididas de Newton");
            return "views/ajuste-curvas/diferencias-divididas/form";
        }
    }
    @GetMapping("/lagrange")
    public String lagrangeForm(Model model) {
        if (!model.containsAttribute("lagrangeDTO")) {
            model.addAttribute("lagrangeDTO", new LagrangeRequest());
        }
        model.addAttribute("titulo", "Interpolación de Lagrange");
        return "views/ajuste-curvas/lagrange/form";
    }

    @PostMapping("/lagrange")
    public String algoritmoLagrange(@ModelAttribute("lagrangeDTO") LagrangeRequest request,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        log.info("Procesando interpolación de Lagrange para N={} puntos", request.getNumeroPuntos());
        try {
            LagrangeRespuesta respuesta = ajusteCurvasService.lagrange(request);
            model.addAttribute("resultado", respuesta);
            model.addAttribute("titulo", "Solución - Polinomio de Lagrange");
            return "views/ajuste-curvas/lagrange/solucion";
        } catch (Exception e) {
            log.error("Error al procesar Polinomio de Lagrange: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("lagrangeDTO", request);
            return "redirect:/ajuste-curvas/lagrange";
        }
    }
    @GetMapping("/regresion-lineal")
    public String regresionLinealForm(@RequestParam(value = "puntos", defaultValue = "4") int puntos, Model model) {
        if (puntos < 2) puntos = 2;

        RegresionLinealRequest request = RegresionLinealRequest.builder()
                .numeroPuntos(puntos)
                .valoresX(new ArrayList<>(Collections.nCopies(puntos, 0.0)))
                .valoresY(new ArrayList<>(Collections.nCopies(puntos, 0.0)))
                .build();

        model.addAttribute("titulo", "Regresión Lineal");
        model.addAttribute("regresionRequest", request);
        return "views/ajuste-curvas/regresion-lineal/form";
    }

    @PostMapping("/regresion-lineal")
    public String algoritmoRegresionLineal(@ModelAttribute("regresionRequest") RegresionLinealRequest request, Model model) {
        try {
            RegresionLinealRespuesta respuesta = ajusteCurvasService.regresionLineal(request);
            model.addAttribute("respuesta", respuesta);
            model.addAttribute("titulo", "Solución - Regresión Lineal");
            return "views/ajuste-curvas/regresion-lineal/solucion";
        } catch (Exception e) {
            log.error("Error ejecutando Regresión Lineal: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("titulo", "Regresión Lineal");
            return "views/ajuste-curvas/regresion-lineal/form";
        }
    }
}