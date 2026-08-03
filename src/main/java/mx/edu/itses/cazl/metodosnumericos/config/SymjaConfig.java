package mx.edu.itses.cazl.metodosnumericos.config;

import org.matheclipse.core.eval.ExprEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración global para registrar el motor de evaluación matemática Symja
 * en el contenedor de Inversión de Control (IoC) de Spring.
 */
@Configuration
public class SymjaConfig {

    @Bean
    public ExprEvaluator exprEvaluator() {
        return new ExprEvaluator();
    }
}