package com.voting.system.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Voting System API")
                .description("API REST para sistema de votação de cooperativas\n\n" +
                            "**Versionamento da API:**\n" +
                            "- **V1** (`/api/v1/*`): Versão básica do sistema\n" +
                            "- **V2** (`/api/v2/*`): Versão com validação externa de CPF e cache\n\n" +
                            "**Como usar o versionamento:**\n" +
                            "- Via URL: `/api/v1/associates` ou `/api/v2/associates`\n" +
                            "- Via Header: `Accept-Version: v1` ou `Accept-Version: v2`\n" +
                            "- Via Query Parameter: `?version=v1` ou `?version=v2`")
                .version("v2.0.0"))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Servidor de Desenvolvimento")));
    }
}
