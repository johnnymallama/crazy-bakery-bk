package uan.edu.co.crazy_bakery.infrastructure.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Crazy Bakery API")
                        .description("API REST para la gestión de la pastelería Crazy Bakery. " +
                                "Permite administrar usuarios, tortas, recetas, ingredientes, " +
                                "órdenes, tamaños y generación de imágenes con IA.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("UAN Especialización")
                                .email("crazy-bakery@uan.edu.co")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("Firebase JWT")
                                .description("Token de autenticación Firebase. Incluir el token JWT obtenido al iniciar sesión.")));
    }
}