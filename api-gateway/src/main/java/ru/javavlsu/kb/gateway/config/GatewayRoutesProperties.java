package ru.javavlsu.kb.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Адреса внутренних сервисов. Фронт ходит только на шлюз и не знает о них.
 */
@Component
@ConfigurationProperties(prefix = "esap.routes")
public class GatewayRoutesProperties {

    private String auth = "http://auth-service:8081";
    private String clinic = "http://clinic-service:8082";
    private String schedule = "http://schedule-service:8083";
    private String core = "http://esap-core:8080";

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getCore() {
        return core;
    }

    public void setCore(String core) {
        this.core = core;
    }
}
