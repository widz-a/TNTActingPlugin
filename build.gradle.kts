plugins {
    java
    `java-library`
    `maven-publish`

    kotlin("jvm").version("2.1.20")
    id("com.gradleup.shadow").version("8.3.6")

    id("io.papermc.paperweight.userdev").version("2.0.0-beta.16")
    id("xyz.jpenilla.run-paper") version("2.3.1") // Adds runServer and runMojangMappedServer tasks for testing
}

group = "${properties["maven_group"]!!}.${name.toLowerCase()}"
version = "${properties["project_version"]}${if (project.hasProperty("devbuild")) ("-" + project.findProperty("devbuild")) else ""}"
description = "${properties["description"]!!}"
val api = properties["pluginApi"]!!

repositories {
    mavenCentral()
    //maven ("https://repo.withicality.xyz")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.inventivetalent.org/repository/public/")
    maven("https://bitbucket.org/kangarko/libraries/raw/master")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://repo.extendedclip.com/releases/")
    maven("https://repo.mikigal.pl/releases")
}

dependencies {
    testImplementation(kotlin("test"))

    val paperVer = "1.21.4"
    compileOnlyApi("io.papermc.paper:paper-api:$paperVer-R0.1-SNAPSHOT")
    paperweight.paperDevBundle("$paperVer-R0.1-SNAPSHOT")
    implementation("com.github.kangarko:foundation:6.9.18") { isTransitive = false }
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.5")
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation("pl.mikigal:ConfigAPI:1.2.4")
    implementation("org.mineskin:java-client:3.0.6-SNAPSHOT")
    implementation("org.mineskin:java-client-jsoup:3.0.6-SNAPSHOT")
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
        archiveBaseName.set("TNTActingPlugin")
        archiveClassifier.set("")
        project.configurations.implementation.get().isCanBeResolved = true
        configurations = listOf(project.configurations.implementation.get())
        //dependsOn(autoRelocate)

        relocateW("fr.mrmicky.fastboard")

        minimize()
    }

    /*jar {
        enabled = false
    }*/

    reobfJar {
        doLast {
            shadowJar.get().archiveFile.get().asFile.delete()
        }
    }

    assemble {
        dependsOn(reobfJar) //bundle
    }

    register("release") {
        dependsOn(build)
    }
}