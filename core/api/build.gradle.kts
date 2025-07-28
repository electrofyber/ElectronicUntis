import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import java.util.Locale

plugins {
	alias(libs.plugins.betteruntis.jvm.library)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.openapi.generator)
}

dependencies {
	implementation(projects.core.model)

	implementation(libs.kotlinx.serialization.json)
	implementation(libs.ktor.client.core)
	implementation(libs.ktor.client.content.negotiation)
	implementation(libs.ktor.serialization)

	testImplementation(libs.junit)
}

// Build Untis Internal OpenAPI specs
val apiSpecList = mutableListOf<File>()
val dir = file("${layout.projectDirectory}/spec/untis-intern/")
dir.walk().filter { it.isFile && Regex("""untis-.*\.yaml""").matches(it.name) }.forEach { file ->
	apiSpecList.add(file)
}
apiSpecList.forEach { file ->
	val apiName = file.name.replace(".yaml", "").replace("untis-", "")
	val taskName = "Untis" + apiName.split('-').joinToString("") { it.replaceFirstChar { c -> c.titlecase(Locale.getDefault()) } }
	val packageName = apiName.replace("-", "_")

	// Register OpenAPI generator task for each API spec
	tasks.register("openApiGenerate$taskName", GenerateTask::class) {
		generatorName.set("kotlin")
		inputSpec.set("${layout.projectDirectory}/spec/untis-intern/untis-$apiName.yaml")
		outputDir.set("${layout.buildDirectory.get()}/generated")
		apiPackage.set("com.sapuseven.untis.core.api.$packageName")
		modelPackage.set("com.sapuseven.untis.model.$packageName")
		configOptions.set(
			mapOf(
				"library" to "jvm-ktor",
				"dateLibrary" to "java8",
				"serializationLibrary" to "kotlinx_serialization"
			)
		)
		typeMappings.set(
			mapOf(
				"java.time.OffsetDateTime" to "com.sapuseven.untis.core.api.serializer.DateTime"
			)
		)
	}

	// Ensure all OpenAPI generation tasks run before compiling Kotlin
	tasks.named("compileKotlin").configure {
		dependsOn("openApiGenerate$taskName")
	}
}

kotlin.sourceSets.named("main") {
	kotlin.srcDir("${layout.buildDirectory.get()}/generated/src/main/kotlin")
}
