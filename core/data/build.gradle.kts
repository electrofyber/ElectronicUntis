plugins {
	alias(libs.plugins.betteruntis.android.library)
	alias(libs.plugins.betteruntis.hilt)
	alias(libs.plugins.kotlin.serialization)
}

android {
	namespace = "com.electrofyber.untis.core.data"
}

dependencies {
	implementation(projects.core.api)
	implementation(projects.core.database)
	implementation(projects.core.datastore)
	implementation(projects.core.domain)
	implementation(projects.core.model)

	implementation(libs.androidx.work)
	implementation(libs.ktor.client.core)
	implementation(libs.ktor.client.cio)
	implementation(libs.ktor.client.auth)
	implementation(libs.kotlinx.datetime)
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.kotlinx.serialization.cbor)
	implementation(libs.andrew0000.cache)

	ksp(libs.hilt.compiler)
}
