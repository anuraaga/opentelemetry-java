plugins {
    id("java-library")
    id("maven-publish")

    id("me.champeau.gradle.jmh")
    id("ru.vyarus.animalsniffer")
}

description = "OpenTelemetry SDK Metrics"
extra["moduleName"] = "io.opentelemetry.sdk.metrics"

dependencies {
    api(project(":api:metrics"))
    api(project(":sdk:common"))

    annotationProcessor("com.google.auto.value:auto-value")

    testAnnotationProcessor("com.google.auto.value:auto-value")

    testImplementation(project(":sdk:testing"))
    testImplementation("com.google.guava:guava")
}

