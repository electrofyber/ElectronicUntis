import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
	alias(libs.plugins.betteruntis.jvm.library)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.openapi.generator)
}

dependencies {
	implementation(projects.core.model)

	implementation(libs.kotlinx.datetime)
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
	val apiName = file.name.removePrefix("untis-").removeSuffix(".yaml")
	val taskName = "openApiGenerate" + apiName.split('-')
		.joinToString("") { it.replaceFirstChar(Char::uppercase) }
	val packageName = apiName.replace("-", "_")

	// Register OpenAPI generator task for each API spec
	val task = tasks.register<GenerateTask>(taskName) {
		generatorName.set("kotlin")
		generateApiTests.set(false)
		generateModelTests.set(false)
		inputSpec.set(file.path)
		outputDir.set("${layout.buildDirectory.get()}/generated/openapi")
		apiPackage.set("com.sapuseven.untis.core.api.$packageName.client")
		modelPackage.set("com.sapuseven.untis.core.api.$packageName.model")
		configOptions.set(
			mapOf(
				"library" to "jvm-ktor",
				"dateLibrary" to "kotlinx-datetime",
				"serializationLibrary" to "kotlinx_serialization",
			)
		)
		typeMappings.set(
			mapOf(
				"DateTime" to "DateTime"
			)
		)
		importMappings.set(
			mapOf(
				"DateTime" to "com.sapuseven.untis.core.api.serializer.DateTime",
			)
		)
	}

	kotlin.sourceSets.named("main") {
		kotlin.srcDir(task)
	}
}
