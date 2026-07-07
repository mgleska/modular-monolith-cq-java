package com.mgleska.mmcqjava2.shared;

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
    public OpenAPI customOpenAPI() {

        final String schemeBearer = "bearer";

        return new OpenAPI()
                .info(new Info()
                        .title("MM-CQ-Java")
                        .description("Modular monolith with Command & Query")
                        .version("0.1"))
                .components(new Components()
                        .addSecuritySchemes(schemeBearer,
                                new SecurityScheme()
                                        .name(schemeBearer)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(schemeBearer));
    }
}