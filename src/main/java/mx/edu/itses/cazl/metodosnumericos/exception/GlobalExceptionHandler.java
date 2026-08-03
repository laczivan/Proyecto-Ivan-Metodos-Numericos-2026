package mx.edu.itses.cazl.metodosnumericos.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controlador global para la captura limpia de excepciones de negocio y matemáticas.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        log.error("Error de validación matemática detectado: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("titulo", "Error en el Cálculo");
        return "views/raices/index";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Error no controlado en la aplicación: ", ex);
        model.addAttribute("errorMessage", "Ocurrió un error inesperado al procesar la solicitud: " + ex.getMessage());
        model.addAttribute("titulo", "Error General");
        return "views/raices/index";
    }
}