package ru.javavlsu.kb.esap.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class SpringDocConfig {
    @Value("${application.version}")
    private String appVersion;

    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(new Info()
                .title("ЕСАП")
                .version(appVersion)
                .description("Единая система автоматизации поликлиник"));
    }
}
