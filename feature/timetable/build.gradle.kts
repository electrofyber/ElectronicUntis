plugins {
	alias(libs.plugins.betteruntis.android.feature)
	alias(libs.plugins.betteruntis.android.library.compose)
}

android {
	namespace = "com.sapuseven.untis.feature.timetable"
}

dependencies {
	implementation(projects.core.domain)
	implementation(libs.androidx.compose.animation)
	implementation(libs.androidx.animation)
	lintChecks(projects.core.lint)
}
