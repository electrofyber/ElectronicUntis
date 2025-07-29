
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.sapuseven.untis.configureFlavors
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

class AndroidFlavorsConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		with(target) {
			extensions.findByType<ApplicationExtension>()?.run {
				configureFlavors(this)
			}

			extensions.findByType<LibraryExtension>()?.run {
				configureFlavors(this)
			}
		}
	}
}
