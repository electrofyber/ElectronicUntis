import com.sapuseven.untis.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		with(target) {
			apply(plugin = "betteruntis.android.library")
			apply(plugin = "betteruntis.hilt")
			apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

			dependencies {
				//"implementation"(project(":core:ui"))
				//"implementation"(project(":core:designsystem"))

				"implementation"(libs.findLibrary("androidx.activity.compose").get())
				"implementation"(libs.findLibrary("androidx.compose.ui").get())
				"implementation"(libs.findLibrary("androidx.compose.material3").get())

				"implementation"(libs.findLibrary("androidx.hilt.navigation.compose").get())
				//"implementation"(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
				"implementation"(libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
				"implementation"(libs.findLibrary("androidx.navigation.compose").get())
				//"implementation"(libs.findLibrary("androidx.tracing.ktx").get())
				"implementation"(libs.findLibrary("kotlinx.serialization.json").get())

				//"testImplementation"(libs.findLibrary("androidx.navigation.testing").get())
				//"androidTestImplementation"(
					//libs.findLibrary("androidx.lifecycle.runtimeTesting").get(),
				//)
			}
		}
	}
}
