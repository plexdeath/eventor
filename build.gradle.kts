plugins {
    `java-gradle-plugin`
    `maven-publish`
}

group = "ru.komus"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    implementation("org.apache.kafka:kafka-clients:3.6.1")
    testImplementation("com.h2database:h2:2.2.224")
    testImplementation("io.cucumber:cucumber-java:7.14.0")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.14.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("io.cucumber:cucumber-java:7.14.0")
    testImplementation("io.cucumber:cucumber-junit:7.14.0")

}

gradlePlugin {
    plugins {
        create("eventorplugin") {
            id = "ru.komus.eventorplugin"
            implementationClass =
                "ru.komus.eventorplugin.processor.EventorProcessorPlugin"
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}