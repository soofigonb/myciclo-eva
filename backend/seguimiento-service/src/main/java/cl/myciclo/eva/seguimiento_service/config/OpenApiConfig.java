package cl.myciclo.eva.seguimiento_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI seguimientoOpenAPI() {
        String esquemaBearer = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("MyCiclo EVA - Seguimiento Service")
                        .version("v1")
                        .description("API REST para la gestión de inicios de ciclo menstrual y bitácoras diarias de síntomas"))
                .addSecurityItem(new SecurityRequirement().addList(esquemaBearer))
                .components(new Components()
                        .addSecuritySchemes(esquemaBearer, new SecurityScheme()
                                .name(esquemaBearer)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
