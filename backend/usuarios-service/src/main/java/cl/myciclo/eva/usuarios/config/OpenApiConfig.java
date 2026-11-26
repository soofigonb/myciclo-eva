package cl.myciclo.eva.usuarios.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String ESQUEMA_BEARER = "bearerAuth";

    @Bean
    public OpenAPI documentacionUsuarios() {
        return new OpenAPI()
                .info(new Info()
                        .title("MyCiclo EVA · Servicio de Usuarios")
                        .version("1.0.0")
                        .description("API de registro, inicio de sesión con JWT y gestión de perfil."))
                .components(new Components()
                        .addSecuritySchemes(
                                ESQUEMA_BEARER,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }
}
