import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	alias(libs.plugins.betteruntis.android.library)
	alias(libs.plugins.betteruntis.hilt)
	alias(libs.plugins.protobuf)
	id("kotlinx-serialization")
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

	generateProtoTasks {
		all().configureEach {
			builtins {
				register("kotlin") {
					option("lite")
				}
				register("java") {
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

dependencies {
	implementation(libs.kotlinx.serialization.json)

	api(libs.androidx.datastore)
	api(libs.protobuf.kotlin.lite)
	api(libs.electrofyber.protostore)
	api(projects.core.model)
	/*
	api(projects.core.datastoreProto)

	implementation(projects.core.common)*/
}
