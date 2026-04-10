package com.terpel.backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI estacionesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Administración de Estaciones — Terpel")
                        .description("""
                                API REST para la gestión de estaciones.

                                **Endpoints disponibles:**
                                - `POST /api/stations` — Crear estación
                                - `GET  /api/stations` — Listar estaciones
                                - `GET  /api/stations/{id}` — Obtener por ID
                                - `PUT  /api/stations/{id}` — Actualizar estación
                                - `DELETE /api/stations/{id}` — Eliminar estación
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Terpel")
                                .email("correo@terpel.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor local")));
    }
}
