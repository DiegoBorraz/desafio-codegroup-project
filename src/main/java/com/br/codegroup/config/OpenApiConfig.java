package com.br.codegroup.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "API do Sistema",
                version = "1.0",
                description = "Documentação da API do Sistema",
                contact = @Contact(
                        name = "Suporte",
                        email = "suporte@empresa.com",
                        url = "https://www.empresa.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Servidor de Desenvolvimento"
                ),
                @Server(
                        url = "https://api.empresa.com",
                        description = "Servidor de Produção"
                )
        }
)
public class OpenApiConfig {
    // Configurações adicionais podem ser feitas aqui
}