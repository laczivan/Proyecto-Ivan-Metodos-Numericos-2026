package mx.edu.itses.cazl.metodosnumericos.controller;

import mx.edu.itses.cazl.metodosnumericos.dto.request.integracion.IntegracionMultipleRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.integracion.Simpson13Request;
import mx.edu.itses.cazl.metodosnumericos.dto.request.integracion.Simpson38Request;
import mx.edu.itses.cazl.metodosnumericos.dto.request.integracion.TrapecioRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.response.integracion.IntegracionMultipleRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.integracion.Simpson13Respuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.integracion.Simpson38Respuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.integracion.TrapecioRespuesta;
import mx.edu.itses.cazl.metodosnumericos.service.IntegracionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/integracion")
@RequiredArgsConstructor
public class IntegracionController {

    private final IntegracionService integracionService;

    @GetMapping({ "", "/" })
    public String index(Model model) {
        return "views/integracion/index";
    }

    @GetMapping("/trapecio")
    public String mostrarFormularioTrapecio(Model model) {
        model.addAttribute("trapecioRequest", new TrapecioRequest("x^2", 0.0, 1.0, 4));
        return "views/integracion/trapecio/form";
    }

    @PostMapping("/trapecio")
    public String calcularTrapecio(@ModelAttribute("trapecioRequest") TrapecioRequest request, Model model) {
        try {
            TrapecioRespuesta respuesta = integracionService.calcularTrapecio(request);
            model.addAttribute("resultado", respuesta);
            return "views/integracion/trapecio/solucion";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "views/integracion/trapecio/form";
        }
    }

    @GetMapping("/simpson13")
    public String mostrarFormularioSimpson13(Model model) {
        model.addAttribute("simpson13Request", new Simpson13Request());
        return "views/integracion/simpson13/form";
    }

    @PostMapping("/simpson13")
    public String calcularSimpson13(@ModelAttribute Simpson13Request request, Model model) {
        try {
            // Asumiendo que inyectaste tu servicio como integracionService
            Simpson13Respuesta respuesta = integracionService.calcularSimpson13(request);
            model.addAttribute("respuesta", respuesta);
            return "views/integracion/simpson13/solucion";
        } catch (Exception e) {
            model.addAttribute("error", "Error al evaluar: " + e.getMessage());
            model.addAttribute("simpson13Request", request);
            return "views/integracion/simpson13/form";
        }
    }

    @GetMapping("/simpson38")
    public String mostrarFormularioSimpson38(Model model) {
        model.addAttribute("simpson38Request", new Simpson38Request());
        return "views/integracion/simpson38/form";
    }

    @PostMapping("/simpson38")
    public String calcularSimpson38(@ModelAttribute Simpson38Request request, Model model) {
        try {
            Simpson38Respuesta respuesta = integracionService.calcularSimpson38(request);
            model.addAttribute("respuesta", respuesta);
            return "views/integracion/simpson38/solucion";
        } catch (Exception e) {
            model.addAttribute("error", "Error al evaluar: " + e.getMessage());
            model.addAttribute("simpson38Request", request);
            return "views/integracion/simpson38/form";
        }
    }

    @GetMapping("/multiple")
    public String mostrarFormularioMultiple(Model model) {
        model.addAttribute("multipleRequest", new IntegracionMultipleRequest());
        return "views/integracion/multiple/form";
    }

    @PostMapping("/multiple")
    public String calcularMultiple(@ModelAttribute("multipleRequest") IntegracionMultipleRequest request, Model model) {
        try {
            IntegracionMultipleRespuesta respuesta = integracionService.calcularMultiple(request);
            model.addAttribute("respuesta", respuesta);
            return "views/integracion/multiple/solucion";
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "views/integracion/multiple/form";
        }
    
    }
}