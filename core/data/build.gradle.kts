plugins {
	alias(libs.plugins.betteruntis.android.library)
	alias(libs.plugins.betteruntis.hilt)
	id("kotlinx-serialization")
}

android {
	namespace = "com.sapuseven.untis.core.data"
	testOptions {
		unitTests {
			isIncludeAndroidResources = true
		}
	}
}

dependencies {
	api(projects.core.model)
	implementation(projects.core.api)
	implementation(projects.core.domain)
	implementation(projects.core.database)
	implementation(projects.core.datastore)

	implementation(libs.andrew0000.cache)
	implementation(libs.kotlinx.serialization.cbor)
	implementation(libs.kotlinx.serialization.json)
}
