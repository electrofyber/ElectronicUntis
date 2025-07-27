import com.android.build.api.dsl.ApplicationExtension
import com.sapuseven.untis.libs
import io.sentry.android.gradle.extensions.SentryPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationSentryConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		with(target) {
			apply(plugin = "io.sentry.android.gradle")

			dependencies {
				"implementation"(libs.findLibrary("sentry.android").get())
				"implementation"(libs.findLibrary("sentry.compose.android").get())
			}

			extensions.configure<ApplicationExtension> {
				defaultConfig.buildConfigField("String", "SENTRY_DSN", "\"https://d3b77222abce4fcfa74fda2185e0f8dc@o1136770.ingest.sentry.io/6188900\"")
			}

			extensions.configure<SentryPluginExtension> {
				autoUploadProguardMapping.set(System.getenv("SENTRY_PROJECT") != null)
			}
		}
	}
}
