import java.io.FileInputStream
import java.util.Date
import java.util.Properties

plugins {
	alias(libs.plugins.betteruntis.android.application)
	alias(libs.plugins.betteruntis.android.application.compose)
	alias(libs.plugins.betteruntis.android.application.sentry)
	alias(libs.plugins.betteruntis.android.flavors)
	alias(libs.plugins.betteruntis.hilt)

	alias(libs.plugins.mikepenz.aboutlibraries)
	alias(libs.plugins.mannodermaus.android.junit5)
	alias(libs.plugins.kotlin.parcelize)
}

// Auto-generates a new version code every minute
fun generateVersionCode(): Int {
	return (Date().time / 1000 / 60).toInt()
}

android {
	namespace = "com.electrofyber.untis"

	defaultConfig {
		applicationId = "com.electrofyber.electrountis"
		versionCode = generateVersionCode()
		versionName = "5.0.0-beta04"

		testInstrumentationRunner = "com.electrofyber.untis.HiltTestRunner"
	}

	signingConfigs {
		val propertiesFile = file("signing.properties")

		val signingProperties = Properties()
		if (propertiesFile.exists()) {
			signingProperties.load(FileInputStream(propertiesFile))
		}

		if (file("BetterUntis.jks").exists()) {
			create("release") {
				storeFile = file("BetterUntis.jks")
				storePassword = signingProperties["keystorePassword"] as String? ?: System.getenv("KEYSTORE_PASSWORD")
				keyAlias = "release"
				keyPassword = signingProperties["keyReleasePassword"] as String? ?: System.getenv("KEY_RELEASE_PASSWORD")
			}

			getByName("debug") {
				storeFile = file("BetterUntis.jks")
				storePassword = signingProperties["keystorePassword"] as String? ?: System.getenv("KEYSTORE_PASSWORD")
				keyAlias = "debug"
				keyPassword = signingProperties["keyDebugPassword"] as String? ?: System.getenv("KEY_DEBUG_PASSWORD")
			}
		}
	}

	buildTypes {
		debug {
			applicationIdSuffix = ".debug"
			versionNameSuffix = "-DEBUG"

			if (file("BetterUntis.jks").exists()) {
				signingConfig = signingConfigs.getByName("debug")
			}
		}

		release {
			isMinifyEnabled = true
			isShrinkResources = true
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

			if (file("BetterUntis.jks").exists()) {
				signingConfig = signingConfigs.getByName("release")
			}
		}
	}

	packaging {
		resources.excludes.add("META-INF/gradle/incremental.annotation.processors")
	}

	lint {
		disable += "MissingTranslation"
	}
}

aboutLibraries {
	collect.includePlatform = false
	library.duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
}

dependencies {
	//implementation(projects.feature.glance)
	implementation(projects.feature.infocenter)
	implementation(projects.feature.login)
	implementation(projects.feature.notifications)
	implementation(projects.feature.roomfinder)
	implementation(projects.feature.settings)
	implementation(projects.feature.timetable)

	implementation(projects.core.api)
	implementation(projects.core.data)
	implementation(projects.core.database)
	implementation(projects.core.datastore)
	implementation(projects.core.domain)
	implementation(projects.core.ui)
	implementation(projects.materialColorUtils)

	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.core.splashscreen)
	implementation(libs.androidx.hilt.navigation.compose)
	implementation(libs.androidx.lifecycle)
	implementation(libs.material)
	implementation(libs.coil)
	implementation(libs.kotlinx.serialization.json)

	// TODO: Move to feature module
	implementation(libs.mikepenz.aboutlibraries.core)
	implementation(libs.mikepenz.aboutlibraries.compose)

	// TODO: Move to feature module
	implementation(libs.androidx.hilt.work)
	implementation(libs.androidx.work)

	// TODO: Move to relevant modules
	implementation(libs.ktor.client.cio)
	implementation(libs.ktor.client.content.negotiation)
	implementation(libs.ktor.serialization)

	// TODO: Move to relevant modules
	implementation(libs.andrew0000.cache)

	ksp(libs.hilt.compiler)

	kspTest(libs.hilt.compiler)
}
