plugins {
	alias(libs.plugins.betteruntis.android.feature)
	alias(libs.plugins.betteruntis.android.library.compose)
}

android {
	namespace = "com.sapuseven.untis.feature.infocenter"
}

dependencies {
	implementation(projects.core.domain)
	implementation(libs.fornewid.placeholder.material3)
	implementation(libs.coil)
	lintChecks(projects.core.lint)
}
