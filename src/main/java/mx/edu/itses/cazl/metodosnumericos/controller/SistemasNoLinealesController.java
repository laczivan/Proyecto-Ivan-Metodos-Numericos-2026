package mx.edu.itses.cazl.metodosnumericos.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.edu.itses.cazl.metodosnumericos.dto.request.sistemasnolineales.NewtonRaphsonNoLinealRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.sistemasnolineales.PuntoFijoNoLinealRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemasnolineales.NewtonRaphsonNoLinealRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemasnolineales.PuntoFijoNoLinealRespuesta;
import mx.edu.itses.cazl.metodosnumericos.service.SistemasNoLinealesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/sistemas-no-lineales")
@RequiredArgsConstructor
public class SistemasNoLinealesController {

    private final SistemasNoLinealesService sistemasNoLinealesService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("titulo", "Sistemas de Ecuaciones No Lineales");
        return "views/sistemas-no-lineales/index";
    }

    // ==========================================
    // FASE 15: ITERACIÓN DE PUNTO FIJO NO LINEAL
    // ==========================================
    @GetMapping({"/punto-fijo", "/punto-fijo/form"})
    public String puntoFijoForm(Model model) {
        PuntoFijoNoLinealRequest request = PuntoFijoNoLinealRequest.builder()
                .tolerancia(0.0001)
                .maximoIteraciones(100)
                .x0(0.0)
                .y0(0.0)
                .build();

        model.addAttribute("titulo", "Punto Fijo - Sistemas No Lineales");
        model.addAttribute("puntoFijoRequest", request);
        return "views/sistemas-no-lineales/punto-fijo/form";
    }

    @PostMapping("/punto-fijo")
    public String algoritmoPuntoFijo(@ModelAttribute("puntoFijoRequest") PuntoFijoNoLinealRequest request, Model model) {
        log.info("Procesando Punto Fijo No Lineal");
        try {
            PuntoFijoNoLinealRespuesta resultado = sistemasNoLinealesService.resolverPuntoFijo(request);
            model.addAttribute("titulo", "Solución - Punto Fijo No Lineal");
            model.addAttribute("resultado", resultado);
            return "views/sistemas-no-lineales/punto-fijo/solucion";
        } catch (Exception e) {
            log.error("Error en Punto Fijo No Lineal: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("titulo", "Punto Fijo - Sistemas No Lineales");
            return "views/sistemas-no-lineales/punto-fijo/form";
        }
    }
    @GetMapping({"/newton-raphson", "/newton-raphson/form"})
    public String newtonRaphsonForm(Model model) {
        NewtonRaphsonNoLinealRequest request = NewtonRaphsonNoLinealRequest.builder()
                .tolerancia(0.0001)
                .maximoIteraciones(100)
                .x0(0.0)
                .y0(0.0)
                .build();

        model.addAttribute("titulo", "Newton-Raphson - Sistemas No Lineales");
        model.addAttribute("newtonRaphsonRequest", request);
        return "views/sistemas-no-lineales/newton-raphson/form";
    }

    @PostMapping("/newton-raphson")
    public String algoritmoNewtonRaphson(@ModelAttribute("newtonRaphsonRequest") NewtonRaphsonNoLinealRequest request, Model model) {
        log.info("Procesando Newton-Raphson No Lineal para f1: '{}', f2: '{}'", request.getF1(), request.getF2());
        try {
            NewtonRaphsonNoLinealRespuesta resultado = sistemasNoLinealesService.resolverNewtonRaphson(request);
            model.addAttribute("titulo", "Solución - Newton-Raphson No Lineal");
            model.addAttribute("resultado", resultado);
            return "views/sistemas-no-lineales/newton-raphson/solucion";
        } catch (Exception e) {
            log.error("Error en Newton-Raphson No Lineal: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("titulo", "Newton-Raphson - Sistemas No Lineales");
            return "views/sistemas-no-lineales/newton-raphson/form";
        }
    }
}