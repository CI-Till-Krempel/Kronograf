
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

plugins {
    `java-library`
}

dependencies {
    // YAML parsing
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.1")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")

    // Semantic versioning
    api("com.vdurmont:semver4j:3.1.0")
    
    // XML Parsing for JUnit reports
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.16.1")
}

// Ensure the core module is a Java/Kotlin library
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
