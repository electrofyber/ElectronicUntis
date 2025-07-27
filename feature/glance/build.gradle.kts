plugins {
	alias(libs.plugins.betteruntis.android.feature)
	alias(libs.plugins.betteruntis.android.library.compose)
}

android {
	namespace = "com.sapuseven.untis.glance"
}

dependencies {
	implementation(libs.androidx.glance)
	implementation(libs.androidx.glance.material3)

	implementation(project(":persistence")) // move to core.data
	implementation(project(":material-color-utils")) // move to core.designsystem
}