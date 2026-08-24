package mx.edu.itses.cazl.metodosnumericos.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.itses.cazl.metodosnumericos.dto.request.derivacion.DerivacionRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.response.derivacion.DerivacionRespuesta;
import mx.edu.itses.cazl.metodosnumericos.service.DerivacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para gestionar las peticiones de diferenciación e integración numérica.
 */
@Slf4j
@Controller
@RequestMapping("/derivacion")
@RequiredArgsConstructor
public class DerivacionController {

    private final DerivacionService derivacionService;

    @GetMapping
    public String index() {
        return "views/derivacion/index";
    }
    
    @GetMapping("/diferenciacion")
    public String mostrarFormulario(Model model) {
        DerivacionRequest request = DerivacionRequest.builder()
                .funcion("x^3 + 2*x^2 - x + 5")
                .x(2.0)
                .h(0.1)
                .ordenDerivada(1)
                .direccion("ADELANTE") // Valor por defecto
                .exactitud("ESTANDAR") // Valor por defecto
                .build();
        
        model.addAttribute("derivacionRequest", request);
        return "views/derivacion/diferenciacion/form";
    }

    @PostMapping("/diferenciacion")
    public String calcularDiferenciacion(@ModelAttribute("derivacionRequest") DerivacionRequest request, Model model) {
        try {
            DerivacionRespuesta respuesta = derivacionService.calcularDiferenciacion(request);
            model.addAttribute("respuesta", respuesta);
            return "views/derivacion/diferenciacion/solucion";
        } catch (Exception e) {
            log.error("Error al calcular diferenciación numérica: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("derivacionRequest", request);
            return "views/derivacion/diferenciacion/form";
        }
    }
}