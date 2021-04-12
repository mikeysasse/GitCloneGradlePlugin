plugins {
    kotlin("jvm") version "1.3.72"
    `maven-publish`
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "io.github.mikeysasse"
version = "1.01"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // Jgit
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.11.0.202103091610-r")

    // Jackson YAML
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.2")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

gradlePlugin {
    plugins {
        create("gitclone") {
            id = "io.github.mikeysasse.gitclone"
            implementationClass = "io.github.mikeysasse.gitclone.GitClonePlugin"
        }
    }
}