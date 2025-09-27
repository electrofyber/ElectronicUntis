plugins {
	alias(libs.plugins.betteruntis.android.feature)
	alias(libs.plugins.betteruntis.android.library.compose)
}

android {
	namespace = "com.sapuseven.untis.feature.settings"
}

dependencies {
	implementation(projects.core.domain)
	implementation(libs.accompanist.permissions)
	implementation(libs.fornewid.material.motion.compose.core)
	implementation(libs.mikepenz.aboutlibraries.core)
	implementation(libs.mikepenz.aboutlibraries.compose)
	lintChecks(projects.core.lint)
}
