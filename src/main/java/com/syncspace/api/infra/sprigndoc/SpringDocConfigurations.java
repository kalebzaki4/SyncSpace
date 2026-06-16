package com.syncspace.api.infra.sprigndoc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfigurations {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira o token JWT gerado no login para acessar os recursos protegidos.")))
                .info(new Info()
                        .title("SyncSpace API")
                        .version("v1.0.0")
                        .description("API REST da aplicação SyncSpace, responsável pelo gerenciamento de usuários, autenticação e controle de reservas de salas.")
                        .contact(new Contact()
                                .name("Kaleb")
                                .email("kaleb@syncspace.com")
                                .url("https://github.com/Kaleb/SyncSpace"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://syncspace.com/api/licenca")));
    }
}