package ru.javavlsu.kb.common.client;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.javavlsu.kb.common.security.EsapProperties;

/**
 * Синхронные межсервисные вызовы. Заголовок Authorization пробрасывается
 * из входящего запроса, поэтому downstream-сервисы видят того же пользователя.
 */
@Configuration
@RequiredArgsConstructor
public class ServiceClientConfig {

    private final EsapProperties esapProperties;

    @Bean
    public ClientHttpRequestInterceptor jwtPropagationInterceptor() {
        return (request, body, execution) -> {
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
                String authorization = attributes.getRequest().getHeader("Authorization");
                if (authorization != null) {
                    request.getHeaders().set("Authorization", authorization);
                }
            }
            return execution.execute(request, body);
        };
    }

    @Bean("authClient")
    public RestClient authClient(RestClient.Builder builder, ClientHttpRequestInterceptor jwtPropagationInterceptor) {
        return builder.clone()
                .baseUrl(esapProperties.getServices().getAuth())
                .requestInterceptor(jwtPropagationInterceptor)
                .build();
    }

    @Bean("clinicClient")
    public RestClient clinicClient(RestClient.Builder builder, ClientHttpRequestInterceptor jwtPropagationInterceptor) {
        return builder.clone()
                .baseUrl(esapProperties.getServices().getClinic())
                .requestInterceptor(jwtPropagationInterceptor)
                .build();
    }

    @Bean("scheduleClient")
    public RestClient scheduleClient(RestClient.Builder builder, ClientHttpRequestInterceptor jwtPropagationInterceptor) {
        return builder.clone()
                .baseUrl(esapProperties.getServices().getSchedule())
                .requestInterceptor(jwtPropagationInterceptor)
                .build();
    }
}
