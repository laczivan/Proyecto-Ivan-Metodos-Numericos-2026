package mx.edu.itses.cazl.metodosnumericos.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador de la capa de presentación para el módulo de Raíces de Ecuaciones.
 */
@Slf4j
@Controller
@RequestMapping("/raices")
@RequiredArgsConstructor
public class RaicesController {

    private final RaicesEcuaciones raicesEcuaciones;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("titulo", "Raíces de Ecuaciones");
        return "views/raices/index";
    }

    // ==========================================
    // FASE 3 Y 4: BISECCIÓN
    // ==========================================
    @GetMapping({"/biseccion", "/biseccion/form"})
    public String biseccionForm(Model model) {
        model.addAttribute("titulo", "Método de Bisección");
        model.addAttribute("biseccion", new Biseccion());
        return "views/raices/biseccion/form";
    }

    @PostMapping({"/biseccion", "/biseccion/form"})
    public String algoritmoBiseccion(@ModelAttribute("biseccion") Biseccion request, Model model) {
        log.info("Procesando Bisección para la función: {}", request.getFx());
        try {
            List<BiseccionRespuesta> resultados = raicesEcuaciones.biseccion(request);
            model.addAttribute("resultados", resultados);
            model.addAttribute("titulo", "Solución - Bisección");
            return "views/raices/biseccion/solucion";
        } catch (IllegalArgumentException e) {
            log.error("Error en Bisección: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "views/raices/biseccion/form";
        }
    }

    // ==========================================
    // FASE 5: REGLA FALSA
    // ==========================================
    @GetMapping({"/reglafalsa", "/regla-falsa"})
    public String reglaFalsaForm(Model model) {
        model.addAttribute("titulo", "Método de Regla Falsa");
        model.addAttribute("reglaFalsa", new ReglaFalsa());
        return "views/raices/reglafalsa/form";
    }

    @PostMapping({"/reglafalsa", "/regla-falsa"})
    public String algoritmoReglaFalsa(@ModelAttribute("reglaFalsa") ReglaFalsa request, Model model) {
        log.info("Procesando Regla Falsa para la función: {}", request.getFx());
        try {
            List<ReglaFalsaRespuesta> resultados = raicesEcuaciones.reglaFalsa(request);
            model.addAttribute("resultados", resultados);
            model.addAttribute("titulo", "Solución - Regla Falsa");
            return "views/raices/reglafalsa/solucion";
        } catch (IllegalArgumentException e) {
            log.error("Error en Regla Falsa: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "views/raices/reglafalsa/form";
        }
    }

    private final RaicesEcuaciones raicesService;
    @GetMapping({"/puntofijo", "/punto-fijo"})
    public String mostrarFormularioPuntoFijo(Model model) {
        model.addAttribute("puntoFijo", new PuntoFijo());
        return "views/raices/puntofijo/form";
    }

    @PostMapping("/puntofijo")
    public String algoritmoPuntoFijo(@ModelAttribute("puntoFijo") PuntoFijo request, Model model) {
        log.info("Punto Fijo - Entrada: X0={}, G(x)='{}', ER={}, MaxIter={}", 
                request.getX0(), request.getGx(), request.getEr(), request.getMaximoIteraciones());

        try {
            List<PuntoFijoRespuesta> resultados = raicesService.puntoFijo(request);
            model.addAttribute("resultados", resultados);
            return "views/raices/puntofijo/solucion";
        } catch (Exception e) {
            log.error("Error ejecutando el algoritmo de Punto Fijo: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error en la evaluación matemática: " + e.getMessage());
            return "views/raices/puntofijo/form";
        }
    }

    @GetMapping({"/newtonraphson", "/newton-raphson"})
    public String newtonRaphsonForm(Model model) {
        if (!model.containsAttribute("newtonRaphson")) {
            model.addAttribute("newtonRaphson", new NewtonRaphson());
        }
        return "views/raices/newtonraphson/form";
    }
    private final RaicesEcuaciones raicesEcuacionesService;
    // 7.3.2 Procesamiento del Algoritmo y Gestión de Excepciones
    @PostMapping("/newtonraphson")
    public String algoritmoNewtonRaphson(@ModelAttribute NewtonRaphson request, 
                                         Model model, 
                                         RedirectAttributes redirectAttributes) {
        log.info("Procesando Newton-Raphson para la función: {}, Xi: {}, ER: {}", 
                 request.getFx(), request.getXi(), request.getEr());
        try {
            List<NewtonRaphsonRespuesta> soluciones = raicesEcuacionesService.newtonRaphson(request);
            model.addAttribute("soluciones", soluciones);
            model.addAttribute("request", request);
            return "views/raices/newtonraphson/solucion";
        } catch (Exception e) {
            log.error("Error al ejecutar el algoritmo Newton-Raphson: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("newtonRaphson", request);
            return "redirect:/raices/newtonraphson";
        }
    }


    @GetMapping("/secante")
    public String secanteForm(Model model) {
        log.info("Cargando formulario del Método de la Secante");
        model.addAttribute("title", "Método de la Secante");
        model.addAttribute("secanteDTO", new Secante());
        return "views/raices/secante/form";
    }

    @PostMapping("/secante/calcular")
    public String algoritmoSecante(@ModelAttribute("secanteDTO") Secante request, Model model) {
        log.info("Procesando algoritmo de la Secante con parámetros: {}", request);
        
        List<SecanteRespuesta> resultados = raicesService.secante(request);

        model.addAttribute("title", "Solución - Método de la Secante");
        model.addAttribute("resultados", resultados);
        return "views/raices/secante/solucion";
    }
    /**
     * Muestra el formulario de entrada para el Método de la Secante Modificado.
     * Rutas: GET /raices/secante-modificada o /raices/secantemodificada
     */
    @GetMapping({"/secante-modificada", "/secantemodificada"})
    public String secanteModificadaForm(Model model) {
        log.info("Cargando formulario para el Método de la Secante Modificado");
        model.addAttribute("title", "Método de la Secante Modificado");
        model.addAttribute("secanteModificadaDTO", new SecanteModificada());
        return "views/raices/secantemodificada/form";
    }

    /**
     * Recibe los parámetros del formulario, invoca la capa de servicio y direcciona a la vista de solución.
     * Ruta: POST /raices/secante-modificada
     */
    @PostMapping("/secante-modificada")
    public String algoritmoSecanteModificada(
            @ModelAttribute("secanteModificadaDTO") SecanteModificada request,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Ejecutando Secante Modificada con parámetros: {}", request);

        try {
            List<SecanteModificadaRespuesta> resultados = raicesEcuacionesService.secanteModificada(request);
            model.addAttribute("title", "Solución - Método de la Secante Modificado");
            model.addAttribute("resultados", resultados);
            return "views/raices/secantemodificada/solucion";

        } catch (Exception e) {
            log.error("Error al ejecutar el algoritmo de Secante Modificada: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/raices/secante-modificada";
        }
    }
    

}