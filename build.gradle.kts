import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("net.fabricmc.fabric-loom-remap")
	`maven-publish`
	id("org.jetbrains.kotlin.jvm") version "2.4.20"
}

repositories {
	exclusiveContent {
		forRepository {
			maven {
				name = "Modrinth"
				url = uri("https://api.modrinth.com/maven")
			}
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}
	maven {
		name = "Shedaniel"
		url = uri("https://maven.shedaniel.me/")
	}
	maven {
		name = "JitPack"
		url = uri("https://jitpack.io")
	}
	maven {
		name = "GeckoLib"
		url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
		content {
			includeGroupByRegex("software\\.bernie.*")
			includeGroup("com.eliotlash.mclib")
		}
	}
}

loom {
	splitEnvironmentSourceSets()

	mods {
		register("magic") {
			sourceSet(sourceSets.main.get())
			sourceSet(sourceSets.getByName("client"))
		}
	}
}

fabricApi {
	configureDataGeneration {
		client = true
	}
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
    mappings(loom.officialMojangMappings())
	modImplementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")

	modImplementation("maven.modrinth:zJpHMkdD:155jtqJi")

	modImplementation("com.github.iPortalTeam.ImmersivePortalsMod:imm_ptl_core:v3.3.9-mc1.20.1") {
		exclude(group = "net.fabricmc.fabric-api")
		isTransitive = false
	}

	modImplementation("me.shedaniel.cloth:cloth-config-fabric:11.0.99") {
		exclude(group = "net.fabricmc.fabric-api")
	}

	modImplementation("com.github.iPortalTeam.ImmersivePortalsMod:q_misc_util:v3.3.9-mc1.20.1") {
		exclude(group = "net.fabricmc.fabric-api")
		isTransitive = false
	}
	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${providers.gradleProperty("fabric_kotlin_version").get()}")
	modImplementation("software.bernie.geckolib:geckolib-fabric-${providers.gradleProperty("minecraft_version").get()}:${providers.gradleProperty("geckolib_version").get()}")
	implementation("com.eliotlash.mclib:mclib:20")
}

kotlin
tasks.processResources {
	val version = version
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand("version" to version)
		// 1. Borramos la línea de acá adentro porque solo protegía al fabric.mod.json
	}

	// 2. CORRECCIÓN CLAVE: Colocamos la regla ACÁ AFUERA.
	// Ahora cubre absolutamente a todos los JSON del mod (incluyendo center_block.json)
	duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 17
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_17
	}
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

tasks.jar {
	val projectName = project.name
	inputs.property("projectName", projectName)

	from("LICENSE") {
		rename { "${it}_$projectName" }
	}
}

// configure the maven publication
publishing {
	publications {
		register<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}
