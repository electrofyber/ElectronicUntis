plugins {
	alias(libs.plugins.betteruntis.jvm.library)
	id("kotlinx-serialization")
}

dependencies {
	implementation(libs.kotlinx.serialization.json)
	api(libs.kotlinx.datetime)
}
