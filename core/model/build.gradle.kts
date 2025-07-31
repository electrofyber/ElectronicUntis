plugins {
	alias(libs.plugins.betteruntis.jvm.library)
}

dependencies {
	implementation(libs.kotlinx.serialization.json)
	api(libs.kotlinx.datetime)
}
