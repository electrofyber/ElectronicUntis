
plugins {
	alias(libs.plugins.betteruntis.android.library)
	id("com.google.devtools.ksp")
}

android {
	namespace = "com.sapuseven.untis.core.domain"
}

dependencies {
	api(projects.core.data)
	api(projects.core.model)
	implementation(projects.core.api)

	implementation(libs.javax.inject)
}
