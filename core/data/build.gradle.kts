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
	implementation(projects.core.api)
	api(projects.core.database)
	api(projects.core.datastore)
	api(projects.core.model)

	implementation(libs.andrew0000.cache)
	implementation(libs.kotlinx.serialization.cbor)
	implementation(libs.kotlinx.serialization.json)
}
