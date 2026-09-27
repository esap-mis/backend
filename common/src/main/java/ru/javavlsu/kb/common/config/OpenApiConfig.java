package ru.javavlsu.kb.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class OpenApiConfig {

    @Value("${application.version:2.0.0}")
    private String appVersion;

    @Value("${application.title:ESAP}")
    private String appTitle;

    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(new Info()
                .title(appTitle)
                .version(appVersion)
                .description("Единая система автоматизации и клиники"));
    }
}
