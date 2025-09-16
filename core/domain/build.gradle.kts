
plugins {
	alias(libs.plugins.betteruntis.android.library)
	id("com.google.devtools.ksp")
}

android {
	namespace = "com.sapuseven.untis.core.domain"
}

dependencies {
	api(projects.core.model)
	api(projects.core.datastore)

	implementation(libs.kotlinx.coroutines)
	implementation(libs.javax.inject)
}
