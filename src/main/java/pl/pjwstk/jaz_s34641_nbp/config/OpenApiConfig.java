package pl.pjwstk.jaz_s34641_nbp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("JAZ s34641 NBP API")
                        .version("1.0.0")
                        .description("API oblicza średni kurs waluty na podstawie danych z API NBP i zapisuje zapytania w bazie danych."));
    }
}