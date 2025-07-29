pluginManagement {
	includeBuild("build-logic")
	repositories {
		google {
			content {
				includeGroupByRegex("com\\.android.*")
				includeGroupByRegex("com\\.google.*")
				includeGroupByRegex("androidx.*")
			}
		}
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
	repositories {
		google {
			content {
				includeGroupByRegex("com\\.android.*")
				includeGroupByRegex("com\\.google.*")
				includeGroupByRegex("androidx.*")
			}
		}
		mavenCentral()
		mavenLocal()
	}
}

rootProject.name = "betteruntis"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":feature:glance")
include(":feature:login")
include(":feature:timetable")
include(":core:api")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:domain")
include(":core:model")
include(":core:ui")

include(":material-color-utils")

check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_17)) {
	"""
    BetterUntis requires JDK 17+ but it is currently using JDK ${JavaVersion.current()}.
    Java Home: [${System.getProperty("java.home")}]
    https://developer.android.com/build/jdks#jdk-config-in-studio
    """.trimIndent()
}
