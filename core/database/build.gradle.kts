plugins {
	alias(libs.plugins.betteruntis.android.library)
	alias(libs.plugins.betteruntis.android.room)
	alias(libs.plugins.betteruntis.hilt)
}

android {
	namespace = "com.sapuseven.untis.core.database"
}

dependencies {
	implementation(libs.andrew0000.cache)

	api(projects.core.api) // TODO: Remove references to :core:api and use :core:model instead
	api(projects.core.model)

	// <editor-fold desc="Fix crash from missing `beginTransactionReadOnly()` method in Room due to sqlite version mismatch">
	// see https://issuetracker.google.com/issues/400483860#comment7
	implementation("androidx.sqlite:sqlite:2.5.1") {
		exclude(group = "io.sentry", module = "sentry-android-sqlite")
	}
	implementation("androidx.sqlite:sqlite-ktx:2.5.1") {
		exclude(group = "io.sentry", module = "sentry-android-sqlite")
	}
	configurations.configureEach {
		resolutionStrategy {
			force("androidx.sqlite:sqlite:2.5.1")
			force("androidx.sqlite:sqlite-ktx:2.5.1")
		}
	}
	// </editor-fold>
}