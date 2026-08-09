package mx.edu.itses.cazl.metodosnumericos.controller;

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
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales.GaussSeidelRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales.JacobiRespuesta;
import mx.edu.itses.cazl.metodosnumericos.service.SistemasLinealesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de la capa de presentación para el módulo de Sistemas Lineales.
 */
@Slf4j
@Controller
@RequestMapping("/sistemas-lineales")
@RequiredArgsConstructor
public class SistemasLinealesController {

    private final SistemasLinealesService sistemasLinealesService;

    /**
     * Mapeo para el índice principal del módulo Sistemas Lineales.
     * Ruta: GET /sistemas-lineales
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("titulo", "Sistemas Lineales");
        return "views/sistemas-lineales/index";
    }

    /**
     * Mapeo para el formulario de cálculo de determinantes.
     * Ruta: GET /sistemas-lineales/determinantes
     */
    @GetMapping({"/determinantes", "/determinantes/form"})
    public String determinantesForm(@RequestParam(name = "dim", defaultValue = "3") int dimension, Model model) {
        if (dimension < 2 || dimension > 4) {
            dimension = 3;
        }

        DeterminanteRequest request = DeterminanteRequest.builder()
                .dimension(dimension)
                .matriz(new double[dimension][dimension])
                .build();

        model.addAttribute("titulo", "Determinantes en Métodos Directos");
        model.addAttribute("determinanteDTO", request);
        return "views/sistemas-lineales/determinantes/form";
    }

    /**
     * Procesamiento del formulario del determinante.
     * Ruta: POST /sistemas-lineales/determinantes/calcular
     */
    @PostMapping("/determinantes/calcular")
    public String algoritmoDeterminante(@ModelAttribute("determinanteDTO") DeterminanteRequest request, Model model) {
        log.info("Procesando determinante para dimensión: {}x{}", request.getDimension(), request.getDimension());
        try {
            DeterminanteRespuesta resultado = sistemasLinealesService.calcularDeterminante(request);
            model.addAttribute("titulo", "Solución - Determinante " + request.getDimension() + "x" + request.getDimension());
            model.addAttribute("resultado", resultado);
            return "views/sistemas-lineales/determinantes/solucion";
        } catch (Exception e) {
            log.error("Error al ejecutar cálculo de determinante: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("titulo", "Determinantes en Métodos Directos");
            return "views/sistemas-lineales/determinantes/form";
        }
    }

    @GetMapping({"/eliminacion-gaussiana", "/eliminacion-gaussiana/form"})
    public String eliminacionGaussianaForm(@RequestParam(name = "dim", defaultValue = "3") int dimension, Model model) {
        if (dimension < 2 || dimension > 4) {
            dimension = 3;
        }

        EliminacionGaussianaRequest request = EliminacionGaussianaRequest.builder()
                .dimension(dimension)
                .matrizA(new double[dimension][dimension])
                .vectorB(new double[dimension])
                .build();

        model.addAttribute("titulo", "Eliminación Gaussiana");
        model.addAttribute("gaussDTO", request);
        return "views/sistemas-lineales/eliminacion-gaussiana/form";
    }

    @PostMapping("/eliminacion-gaussiana/calcular")
    public String algoritmoEliminacionGaussiana(@ModelAttribute("gaussDTO") EliminacionGaussianaRequest request, Model model) {
        log.info("Procesando Eliminación Gaussiana para dimensión: {}x{}", request.getDimension(), request.getDimension());
        try {
            EliminacionGaussianaRespuesta resultado = sistemasLinealesService.eliminacionGaussiana(request);
            model.addAttribute("titulo", "Solución - Eliminación Gaussiana " + request.getDimension() + "x" + request.getDimension());
            model.addAttribute("resultado", resultado);
            return "views/sistemas-lineales/eliminacion-gaussiana/solucion";
        } catch (Exception e) {
            log.error("Error al ejecutar Eliminación Gaussiana: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("titulo", "Eliminación Gaussiana");
            return "views/sistemas-lineales/eliminacion-gaussiana/form";
        }
    }
    @GetMapping({"/gauss-jordan", "/gauss-jordan/form"})
    public String gaussJordanForm(@RequestParam(name = "dim", defaultValue = "3") int dimension, Model model) {
        if (dimension < 2 || dimension > 4) {
            dimension = 3;
        }

        GaussJordanRequest request = GaussJordanRequest.builder()
                .dimension(dimension)
                .matrizA(new double[dimension][dimension])
                .vectorB(new double[dimension])
                .build();

        model.addAttribute("titulo", "Método de Gauss-Jordan");
        model.addAttribute("gaussJordanDTO", request);
        return "views/sistemas-lineales/gauss-jordan/form";
    }

    @PostMapping("/gauss-jordan/calcular")
    public String algoritmoGaussJordan(@ModelAttribute("gaussJordanDTO") GaussJordanRequest request, Model model) {
        log.info("Ejecutando algoritmo Gauss-Jordan para dimensión {}x{}", request.getDimension(), request.getDimension());
        try {
            GaussJordanRespuesta resultado = sistemasLinealesService.gaussJordan(request);
            model.addAttribute("titulo", "Solución - Gauss-Jordan " + request.getDimension() + "x" + request.getDimension());
            model.addAttribute("resultado", resultado);
            return "views/sistemas-lineales/gauss-jordan/solucion";
        } catch (Exception e) {
            log.error("Error al procesar Gauss-Jordan: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("titulo", "Método de Gauss-Jordan");
            return "views/sistemas-lineales/gauss-jordan/form";
        }
    }

    @GetMapping({"/jacobi", "/jacobi/form"})
    public String jacobiForm(@RequestParam(name = "dim", defaultValue = "3") int dim, Model model) {
        if (dim < 2 || dim > 4) {
            dim = 3;
        }
        JacobiRequest request = new JacobiRequest();
        request.setDimension(dim);
        request.setMatrizA(new double[dim][dim]);
        request.setVectorB(new double[dim]);
        request.setVectorX0(new double[dim]);
        request.setTolerancia(0.0001);
        request.setMaximoIteraciones(100);

        model.addAttribute("titulo", "Método Iterativo de Jacobi");
        model.addAttribute("jacobiRequest", request);
        return "views/sistemas-lineales/jacobi/form";
    }

    @PostMapping("/jacobi")
    public String algoritmoJacobi(@ModelAttribute("jacobiRequest") JacobiRequest request, Model model) {
        log.info("Procesando Método de Jacobi para dimensión {}x{}", request.getDimension(), request.getDimension());
        try {
            JacobiRespuesta resultado = sistemasLinealesService.jacobi(request);
            model.addAttribute("titulo", "Solución - Método de Jacobi");
            model.addAttribute("resultado", resultado);
            return "views/sistemas-lineales/jacobi/solucion";
        } catch (Exception e) {
            log.error("Error en Método de Jacobi: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("titulo", "Método Iterativo de Jacobi");
            return "views/sistemas-lineales/jacobi/form";
        }
    }
    @GetMapping({"/gauss-seidel", "/gauss-seidel/form"})
    public String gaussSeidelForm(@RequestParam(name = "dim", defaultValue = "3") int dim, Model model) {
        if (dim < 2 || dim > 4) {
            dim = 3;
        }
        GaussSeidelRequest request = new GaussSeidelRequest();
        request.setDimension(dim);
        request.setMatrizA(new double[dim][dim]);
        request.setVectorB(new double[dim]);
        request.setVectorX0(new double[dim]);
        request.setTolerancia(0.0001);
        request.setMaximoIteraciones(100);

        model.addAttribute("titulo", "Método Iterativo de Gauss-Seidel");
        model.addAttribute("gaussSeidelRequest", request);
        return "views/sistemas-lineales/gauss-seidel/form";
    }

    @PostMapping("/gauss-seidel")
    public String algoritmoGaussSeidel(@ModelAttribute("gaussSeidelRequest") GaussSeidelRequest request, Model model) {
        log.info("Procesando Método de Gauss-Seidel para dimensión {}x{}", request.getDimension(), request.getDimension());
        try {
            GaussSeidelRespuesta resultado = sistemasLinealesService.gaussSeidel(request);
            model.addAttribute("titulo", "Solución - Método de Gauss-Seidel");
            model.addAttribute("resultado", resultado);
            return "views/sistemas-lineales/gauss-seidel/solucion";
        } catch (Exception e) {
            log.error("Error en Método de Gauss-Seidel: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("titulo", "Método Iterativo de Gauss-Seidel");
            return "views/sistemas-lineales/gauss-seidel/form";
        }
    }
    
}