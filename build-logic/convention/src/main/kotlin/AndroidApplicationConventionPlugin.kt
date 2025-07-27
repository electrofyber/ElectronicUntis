
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.sapuseven.untis.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		with(target) {
			apply(plugin = "com.android.application")
			apply(plugin = "org.jetbrains.kotlin.android")
			//apply(plugin = "betteruntis.android.lint")

			extensions.configure<ApplicationExtension> {
				configureKotlinAndroid(this)
				defaultConfig.minSdk = 21
				defaultConfig.targetSdk = 35

				// Per-app language support
				@Suppress("UnstableApiUsage")
				androidResources.generateLocaleConfig = true

				testOptions.animationsDisabled = true
				//configureGradleManagedDevices(this)
			}
			extensions.configure<ApplicationAndroidComponentsExtension> {
				//configurePrintApksTask(this)
				//configureBadgingTasks(extensions.getByType<BaseExtension>(), this)
			}
		}
	}
}
