plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

/**
 * Spring Cloud под Spring Boot 4.x существует только в виде milestone
 * 2026.0.0-M1 (gateway 5.1.0-M1), собранного против Boot 4.2.0-M2.
 * Стартер в этой ветке переименован в spring-cloud-starter-gateway-server-webflux.
 * При смене train Spring Cloud обновите версию здесь.
 *
 * Micrometer-реестры намеренно не подключаются: milestone-BOM Spring Cloud
 * тянет Micrometer 1.18.0-M2, а Boot 4.1.x управляет 1.17.1, и версии
 * разъезжаются. Метрики шлюза доступны через actuator встроенными метриками.
 */
extra["springCloudVersion"] = "5.1.0-M1"

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux:${property("springCloudVersion")}")
    implementation("com.auth0:java-jwt:4.3.0")
}

dependencyManagement {
    imports {
        // Сначала Boot: явный список imports вытесняет BOM, который добавляет
        // сам Spring Boot-плагин.
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.1.1")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}
