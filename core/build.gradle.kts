// The core module applies the necessary plugins for a Kotlin library.
plugins {
    kotlin("jvm")
    `java-library`
}

// All repositories are defined here.
repositories {
    mavenCentral()
}

// All dependencies for the core module are declared here.
dependencies {
    // Standard libraries
    implementation(kotlin("stdlib"))

    // YAML parsing
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.1")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")

    // XML Parsing for JUnit reports
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.16.1")
    
    // Semantic versioning
    api("com.vdurmont:semver4j:3.1.0")

    // Testing libraries
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1") // Corrected version
}

// Configure the Java and Kotlin compilers.
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

// Configure the test runner.
tasks.withType<Test> {
    useJUnitPlatform()
}
