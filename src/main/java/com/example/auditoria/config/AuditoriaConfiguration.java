package com.example.auditoria.config;

import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHistorialUseCase;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.ObtenerDashboardAuditoriaUseCase;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.RegistrarHallazgoUseCase;
import com.example.auditoria.usecase.impl.CerrarHallazgoService;
import com.example.auditoria.usecase.impl.ConsultarHallazgoService;
import com.example.auditoria.usecase.impl.ConsultarHistorialService;
import com.example.auditoria.usecase.impl.IniciarRemediacionService;
import com.example.auditoria.usecase.impl.ObtenerDashboardAuditoriaService;
import com.example.auditoria.usecase.impl.ReabrirHallazgoService;
import com.example.auditoria.usecase.impl.RegistrarHallazgoService;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wiring explicito: los casos de uso no llevan anotaciones de Spring, asi que es
 * este circulo exterior quien los instancia y los entrega al contenedor.
 */
@Configuration
public class AuditoriaConfiguration {

    @Bean
    public RegistrarHallazgoUseCase registrarHallazgoUseCase(HallazgoRepositoryPort repo) {
        return new RegistrarHallazgoService(repo);
    }

    @Bean
    public IniciarRemediacionUseCase iniciarRemediacionUseCase(HallazgoRepositoryPort repo,
                                                                HistorialAuditoriaPort historial) {
        return new IniciarRemediacionService(repo, historial);
    }

    @Bean
    public CerrarHallazgoUseCase cerrarHallazgoUseCase(HallazgoRepositoryPort repo,
                                                        HistorialAuditoriaPort historial) {
        return new CerrarHallazgoService(repo, historial);
    }

    @Bean
    public ReabrirHallazgoUseCase reabrirHallazgoUseCase(HallazgoRepositoryPort repo,
                                                          HistorialAuditoriaPort historial) {
        return new ReabrirHallazgoService(repo, historial);
    }

    @Bean
    public ConsultarHallazgoUseCase consultarHallazgoUseCase(HallazgoRepositoryPort repo) {
        return new ConsultarHallazgoService(repo);
    }

    @Bean
    public ObtenerDashboardAuditoriaUseCase obtenerDashboardAuditoriaUseCase(HallazgoRepositoryPort repo) {
        return new ObtenerDashboardAuditoriaService(repo);
    }

    @Bean
    public ConsultarHistorialUseCase consultarHistorialUseCase(HallazgoRepositoryPort repo,
                                                                HistorialAuditoriaPort historial) {
        return new ConsultarHistorialService(repo, historial);
    }
}
