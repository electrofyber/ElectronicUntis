plugins {
	alias(libs.plugins.betteruntis.android.library)
	alias(libs.plugins.betteruntis.android.library.compose)
	//alias(libs.plugins.betteruntis.hilt)
}

android {
	namespace = "com.sapuseven.untis.core.ui"
}

dependencies {
	api(projects.core.model)

//	api(libs.androidx.compose.foundation)
//	api(libs.androidx.compose.foundation.layout)
//	api(libs.androidx.compose.material.iconsExtended)
	api(libs.accompanist.flowlayout)
	api(libs.androidx.activity.compose)
	api(libs.androidx.compose.ui)
	api(libs.androidx.compose.material3)
	api(libs.androidx.compose.material.icons)
//	api(libs.androidx.compose.runtime)
}
