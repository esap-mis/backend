plugins {
    java
    kotlin("jvm") version "2.3.21" apply false
    kotlin("plugin.spring") version "2.3.21" apply false
    id("org.springframework.boot") version "4.1.1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "ru.javavlsu.kb"
    version = "2.0.0"
}

/**
 * Образы собираются Cloud Native Buildpacks через задачу bootBuildImage
 * самого Spring Boot-плагина, поэтому Dockerfile в проекте не нужен.
 * Агрегатная задача позволяет собрать все сервисы одной командой.
 */
val containerModules = listOf(
    "api-gateway",
    "auth-service",
    "clinic-service",
    "schedule-service",
    "esap-core",
    "notification-service",
)

tasks.register("bootBuildImage") {
    group = "build"
    description = "Builds the container image of every service."
    dependsOn(containerModules.map { ":$it:bootBuildImage" })
}
