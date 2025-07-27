import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	`kotlin-dsl`
	//alias(libs.plugins.android.lint)
}

group = "com.sapuseven.untis.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_17
	}
}

dependencies {
	compileOnly(libs.plugin.android)
//	compileOnly(libs.android.tools.common)
	compileOnly(libs.plugin.compose)
	compileOnly(libs.plugin.kotlin)
	compileOnly(libs.plugin.ksp)
	compileOnly(libs.plugin.room)
//	implementation(libs.truth)
//	lintChecks(libs.androidx.lint.gradle)
}

tasks {
	validatePlugins {
		enableStricterValidation = true
		failOnWarning = true
	}
}

gradlePlugin {
	plugins {
//		register("androidApplication") {
//			id = libs.plugins.betteruntis.android.application.asProvider().get().pluginId
//			implementationClass = "AndroidApplicationConventionPlugin"
//		}
//		register("androidApplicationCompose") {
//			id = libs.plugins.betteruntis.android.application.compose.get().pluginId
//			implementationClass = "AndroidApplicationComposeConventionPlugin"
//		}
//		register("androidApplicationJacoco") {
//			id = libs.plugins.betteruntis.android.application.jacoco.get().pluginId
//			implementationClass = "AndroidApplicationJacocoConventionPlugin"
//		}
		register("androidFeature") {
			id = libs.plugins.betteruntis.android.feature.get().pluginId
			implementationClass = "AndroidFeatureConventionPlugin"
		}
		register("androidLibrary") {
			id = libs.plugins.betteruntis.android.library.asProvider().get().pluginId
			implementationClass = "AndroidLibraryConventionPlugin"
		}
		register("androidLibraryCompose") {
			id = libs.plugins.betteruntis.android.library.compose.get().pluginId
			implementationClass = "AndroidLibraryComposeConventionPlugin"
		}
//		register("androidLibraryJacoco") {
//			id = libs.plugins.betteruntis.android.library.jacoco.get().pluginId
//			implementationClass = "AndroidLibraryJacocoConventionPlugin"
//		}
//		register("androidTest") {
//			id = libs.plugins.betteruntis.android.test.get().pluginId
//			implementationClass = "AndroidTestConventionPlugin"
//		}
		register("hilt") {
			id = libs.plugins.betteruntis.hilt.get().pluginId
			implementationClass = "HiltConventionPlugin"
		}
		register("androidRoom") {
			id = libs.plugins.betteruntis.android.room.get().pluginId
			implementationClass = "AndroidRoomConventionPlugin"
		}
//		register("androidFlavors") {
//			id = libs.plugins.betteruntis.android.application.flavors.get().pluginId
//			implementationClass = "AndroidApplicationFlavorsConventionPlugin"
//		}
//		register("androidLint") {
//			id = libs.plugins.betteruntis.android.lint.get().pluginId
//			implementationClass = "AndroidLintConventionPlugin"
//		}
		register("jvmLibrary") {
			id = libs.plugins.betteruntis.jvm.library.get().pluginId
			implementationClass = "JvmLibraryConventionPlugin"
		}
	}
}
