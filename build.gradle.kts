plugins {
    java
    `java-library`
    `maven-publish`

    kotlin("jvm").version("1.8.0")
    id("com.github.johnrengelman.shadow").version("7.1.0")

    id("io.papermc.paperweight.userdev").version("1.5.5")
    id("xyz.jpenilla.run-paper") version("2.2.0") // Adds runServer and runMojangMappedServer tasks for testing
}

group = "${properties["maven_group"]!!}.${name.toLowerCase()}"
version = "${properties["project_version"]}${if (project.hasProperty("devbuild")) ("-" + project.findProperty("devbuild")) else ""}"
description = "${properties["description"]!!}"
val api = properties["pluginApi"]!!

repositories {
    mavenCentral()
    maven ("https://repo.withicality.xyz")
    maven ("https://repo.papermc.io/repository/maven-public/")
    maven ("https://jitpack.io")
    maven ("https://bitbucket.org/kangarko/libraries/raw/master")
    maven ("https://repo.aikar.co/content/groups/aikar/")
    maven ("https://repo.mikigal.pl/releases")
    maven ("https://hub.spigotmc.org/nexus/content/groups/public/")
}

dependencies {
    testImplementation(kotlin("test"))

    val paperVer = "1.20.1"
    compileOnlyApi("io.papermc.paper:paper-api:$paperVer-R0.1-SNAPSHOT")
    paperweight.paperDevBundle("$paperVer-R0.1-SNAPSHOT")

    implementation("io.papermc:paperlib:1.0.7")

    implementation("com.github.kangarko:foundation:6.4.6") { isTransitive = false }
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("pl.mikigal:ConfigAPI:1.2.4")
}

val targetJavaVersion = 17
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

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = javaVersion.toString()
            javaParameters = true
            freeCompilerArgs = listOf("-Xjvm-default=all")
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
        fun relocateW(packag: String) { relocate(packag, "${project.group}.shaded.${packag}") }
        archiveClassifier.set("")
        project.configurations.implementation.get().isCanBeResolved = true
        configurations = listOf(project.configurations.implementation.get())
        //dependsOn(autoRelocate)

        relocateW("fr.mrmicky.fastboard")

        minimize()
    }

    jar {
        enabled = false
    }

    reobfJar {
        doLast {
            shadowJar.get().archiveFile.get().asFile.delete()
        }
    }

    assemble {
        dependsOn(reobfJar) //bundle
    }

    publishing {
        repositories {
            maven {
                name = "withicality-maven"
                val mavenUrl = "https://repo.withicality.xyz"
                url = uri(if (project.hasProperty("devbuild")) "${mavenUrl}/snapshots" else "${mavenUrl}/releases")

                credentials {
                    username = System.getenv("MAVEN_ALIAS")
                    password = System.getenv("MAVEN_TOKEN")
                }

                authentication.register("basic", BasicAuthentication::class)
            }
        }

        publications {
            create<MavenPublication>("mavenJar") {
                artifactId = project.name.toLowerCase()
                version = project.version.toString()
                group = project.group
                artifact(reobfJar)
            }
        }
    }

    register("release") {
        dependsOn(build)
    }
}