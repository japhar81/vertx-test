import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "4.0.3"
}

repositories {
    mavenCentral()
}

val vertxVersion = "3.8.2"
val junitVersion = "5.3.2"
val micrometerVersion = "1.3.0"
val openTracingVersion = "0.33.0"
val elkAPMVersion = "1.10.0"
val slf4jVersion = "1.7.28"
val log4jVersion = "2.12.1"
val jacksonVersion = "2.9.8"

dependencies {
    implementation("io.vertx:vertx-core:$vertxVersion")
    implementation("io.vertx:vertx-web:$vertxVersion")
    implementation("io.vertx:vertx-micrometer-metrics:$vertxVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometerVersion")
    implementation("io.opentracing:opentracing-api:$openTracingVersion")
    implementation("co.elastic.apm:apm-agent-attach:$elkAPMVersion")
    implementation("co.elastic.apm:apm-opentracing:$elkAPMVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.appenders.log4j:log4j2-elasticsearch-jest:1.3.5")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")

    testImplementation("io.vertx:vertx-junit5:$vertxVersion")
    testImplementation("io.vertx:vertx-web-client:$vertxVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "io.vertx.core.Launcher"
}

val mainVerticleName = "MainVerticle"
val watchForChange = "src/**/*.java"
val doOnChange = "${projectDir}/gradlew classes"

tasks {
    test {
        useJUnitPlatform()
    }

    getByName<JavaExec>("run") {
        args = listOf("run", mainVerticleName, "--redeploy=${watchForChange}", "--launcher-class=${application.mainClassName}", "--on-redeploy=${doOnChange}")
    }

    withType<ShadowJar> {
        classifier = "fat"
        manifest {
            attributes["Main-Verticle"] = mainVerticleName
        }
        mergeServiceFiles {
            include("META-INF/services/io.vertx.core.spi.VerticleFactory")
        }
    }
}