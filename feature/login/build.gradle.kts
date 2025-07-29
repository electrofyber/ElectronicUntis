plugins {
	alias(libs.plugins.betteruntis.android.feature)
	alias(libs.plugins.betteruntis.android.flavors)
	alias(libs.plugins.betteruntis.android.library.compose)
}

android {
	namespace = "com.sapuseven.untis.feature.login"
}

dependencies {
	implementation(projects.core.domain)
	implementation(libs.zxing)

	gmsImplementation(libs.gms.code.scanner)
}
