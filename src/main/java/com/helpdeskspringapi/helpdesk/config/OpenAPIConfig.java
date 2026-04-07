package com.helpdeskspringapi.helpdesk.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer")
public class OpenAPIConfig {

    @Bean
    public OpenAPI helpdeskAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Helpdesk API")
                        .description("REST API for managing support tickets, users, categories and roles. Features OAuth2/JWT authentication, role-based access control, and real-time WhatsApp/SMS notifications via Twilio.")
                        .version("v1.0.0")
                        .license(new License()
                                .name("MIT")
                                .url("https://github.com/tonicostmarco/helpdesk-spring-api")));
    }
}
