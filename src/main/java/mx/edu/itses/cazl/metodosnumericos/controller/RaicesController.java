package mx.edu.itses.cazl.metodosnumericos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import mx.edu.itses.cazl.metodosnumericos.dto.request.raices.Biseccion;
import mx.edu.itses.cazl.metodosnumericos.dto.response.raices.BiseccionRespuesta;
import mx.edu.itses.cazl.metodosnumericos.service.RaicesEcuaciones;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/raices")
@RequiredArgsConstructor // Genera el constructor necesario para la inyección de dependencias automática
public class RaicesController {

    // Capa de Servicio inyectada mediante el constructor de Lombok
    private final RaicesEcuaciones raicesService;

    // 2.3 Direcciona a la raíz de /views/raices/index.html
    @GetMapping
    public String index() {
        return "views/raices/index";
    }

    // 2.4 Métodos cerrados
    @GetMapping("/biseccion")
    public String biseccion(Model model) {
        model.addAttribute("biseccion", new Biseccion());
        // Direcciona la salida hacia form.html
        return "views/raices/biseccion/form"; 
    }

    // Método de procesamiento modificado para la Fase 4
    @PostMapping("/biseccion")
    public String algoritmoBiseccion(@ModelAttribute Biseccion biseccionRequest, Model model) {
        // Imprime en la consola mediante Lombok los valores recibidos
        log.info("Valores recibidos para Bisección:");
        log.info("XL: {}", biseccionRequest.getXl()); 
        log.info("XU: {}", biseccionRequest.getXu()); 
        log.info("FX: {}", biseccionRequest.getFx()); 
        log.info("ER: {}", biseccionRequest.getEr()); 
        log.info("MaximoIteraciones: {}", biseccionRequest.getMaximoIteraciones()); 

        // 1. Invocamos la lógica matemática pasándole los parámetros recibidos
        List<BiseccionRespuesta> resultados = raicesService.biseccion(biseccionRequest);

        // 2. Enviamos la lista de objetos resultantes al modelo de Thymeleaf
        model.addAttribute("resultados", resultados);

        // 3. Direccionamos la salida hacia la nueva plantilla 'solucion.html'
        return "views/raices/biseccion/solucion"; 
    }

    @GetMapping("/regla-falsa")
    public String reglaFalsa() {
        return "views/raices/empty";
    }

    // 2.4 Métodos abiertos
    @GetMapping("/punto-fijo")
    public String puntoFijo() {
        return "views/raices/empty";
    }

    @GetMapping("/newton-raphson")
    public String newtonRaphson() {
        return "views/raices/empty";
    }

    @GetMapping("/secante")
    public String secante() {
        return "views/raices/empty";
    }

    @GetMapping("/secante-modificado")
    public String secanteModificado() {
        return "views/raices/empty";
    }
}