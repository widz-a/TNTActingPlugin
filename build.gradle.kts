import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`
    `maven-publish`

    kotlin("jvm").version("2.3.20")
    id("com.gradleup.shadow").version("9.4.1")

    //id("io.papermc.paperweight.userdev").version("2.0.0-beta.18")
    id("xyz.jpenilla.run-paper") version("3.0.2") // Adds runServer and runMojangMappedServer tasks for testing
}

group = "${properties["maven_group"]!!}.${name.lowercase()}"
version = "${properties["project_version"]}${if (project.hasProperty("devbuild")) ("-" + project.findProperty("devbuild")) else ""}"
description = "${properties["description"]!!}"
val api = properties["pluginApi"]!!

repositories {
    mavenCentral() //LuckPerms
    maven("https://repo.papermc.io/repository/maven-public/") //Paper ofc
    maven("https://jitpack.io") //Foundation
    maven("https://repo.inventivetalent.org/repository/public/") //MineSkin
    maven("https://repo.aikar.co/content/groups/aikar/") //ACF
    maven("https://repo.extendedclip.com/releases/") //PAPI
    maven("https://repo.mikigal.pl/releases") //ConfigAPI
}

dependencies {
    testImplementation(kotlin("test"))

    compileOnlyApi("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    //paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    implementation("com.github.kangarko:foundation:6.9.22") { isTransitive = false }
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.5")
    compileOnly("me.clip:placeholderapi:2.12.2")
    implementation("pl.mikigal:ConfigAPI:1.2.6")
    implementation("org.mineskin:java-client:3.2.5")
    implementation("org.mineskin:java-client-jsoup:3.2.5")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    test {
        useJUnitPlatform()
    }

    val javaVersion = JavaVersion.toVersion(targetJavaVersion)

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }

    withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))
            javaParameters = true
            freeCompilerArgs.add("-jvm-default=enable")
        }
    }

    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        if (JavaVersion.current() < javaVersion) {
            toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
        }
    }

    processResources {
        val props = mapOf("name" to project.name, "version" to project.version, "group" to project.group, "api" to api, "description" to project.description)

        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("**/plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        //fun relocateW(packag: String) { relocate(packag, "${project.group}.shaded.${packag}") }
        archiveBaseName.set("TNTActingPlugin")
        archiveClassifier.set("")
        configurations = listOf(project.configurations.runtimeClasspath.get())
        //dependsOn(autoRelocate)

        //relocateW("fr.mrmicky.fastboard")

        minimize()
    }

    /*jar {
        enabled = false
    }*/

    /*reobfJar {
        doLast {
            shadowJar.get().archiveFile.get().asFile.delete()
        }
    }

    assemble {
        dependsOn(reobfJar) //bundle
    }*/

    register("release") {
        dependsOn(build)
    }
}