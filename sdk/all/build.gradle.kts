plugins {
    id("java-library")
    id("maven-publish")

    id("me.champeau.gradle.jmh")
    id("ru.vyarus.animalsniffer")
}

description = "OpenTelemetry SDK"
extra["moduleName"] = "io.opentelemetry.sdk"
base.archivesBaseName = "opentelemetry-sdk"

dependencies {
    api(project(":api:all"))
    api(project(":sdk:common"))
    api(project(":sdk:trace"))

    annotationProcessor("com.google.auto.value:auto-value")

    testAnnotationProcessor("com.google.auto.value:auto-value")

    testImplementation(project(":sdk:testing"))
}
