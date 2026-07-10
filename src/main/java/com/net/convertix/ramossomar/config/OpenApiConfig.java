package com.net.convertix.ramossomar.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI openAPI() {
		final String schemeName = "bearer-jwt";

		return new OpenAPI()
				.info(new Info()
						.title("API Ramos Somar")
						.description("Backend do projeto Ramos Somar — gestão de usuários, apoiadores, publicações e tokens.")
						.version("1.0.0")
						.contact(new Contact()
								.name("Convertix")
								.email("contato@convertix.net")))
				.addSecurityItem(new SecurityRequirement().addList(schemeName))
				.components(new Components()
						.addSecuritySchemes(schemeName, new SecurityScheme()
								.name(schemeName)
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
								.description("Informe o token JWT obtido no endpoint de login")));
	}
}
