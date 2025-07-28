import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	alias(libs.plugins.betteruntis.android.library)
	alias(libs.plugins.betteruntis.hilt)
	alias(libs.plugins.protobuf)
}

android {
	namespace = "com.sapuseven.untis.core.datastore"

	defaultConfig {
		consumerProguardFiles("consumer-proguard-rules.pro")
	}
}

protobuf {
	protoc {
		artifact = libs.protobuf.protoc.get().toString()
	}

	// Generates the java Protobuf-lite code for the Protobufs in this project. See
	// https://github.com/google/protobuf-gradle-plugin#customizing-protobuf-compilation
	// for more information.
	generateProtoTasks {
		all().configureEach {
			builtins {
				register("kotlin") {
					option("lite")
				}
			}
		}
	}

	androidComponents {
		onVariants(selector().all()) { variant ->
			afterEvaluate {
				val capName = variant.name.capitalized()
				tasks.getByName<KotlinCompile>("ksp${capName}Kotlin") {
					setSource(tasks.getByName("generate${capName}Proto").outputs)
				}
			}
		}
	}
}

/*androidComponents.beforeVariants {
	android.sourceSets.register(it.name) {
		val buildDir = layout.buildDirectory.get().asFile
		java.srcDir(buildDir.resolve("generated/source/proto/${it.name}/java"))
		kotlin.srcDir(buildDir.resolve("generated/source/proto/${it.name}/kotlin"))
	}
}*/


dependencies {
	implementation(libs.protobuf.kotlin.lite)
	implementation(libs.sapuseven.protostore)

	/*api(libs.androidx.dataStore)
	api(projects.core.datastoreProto)
	api(projects.core.model)

	implementation(projects.core.common)

	testImplementation(projects.core.datastoreTest)
	testImplementation(libs.kotlinx.coroutines.test)*/
}
