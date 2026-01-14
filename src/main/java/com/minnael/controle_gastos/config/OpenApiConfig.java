package com.minnael.controle_gastos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Controle de Gastos")
                        .version("1.0.0")
                        .description("API REST para gerenciamento de gastos por categoria. " +
                                "Permite criar, listar, atualizar e deletar categorias e gastos, " +
                                "com cálculo automático do gasto mensal por categoria.")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("contato@controlegastos.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.controlegastos.com")
                                .description("Servidor de Produção")
                ));
    }
}
