package com.sapuseven.untis.feature.settings.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.sapuseven.untis.core.domain.navigation.FeatureRoute
import com.sapuseven.untis.core.domain.navigation.FeatureRouteDsl
import com.sapuseven.untis.core.domain.navigation.FeatureRouteItem
import com.sapuseven.untis.feature.settings.R
import com.sapuseven.untis.feature.settings.SettingsCategoryScreen
import com.sapuseven.untis.feature.settings.fragments.SettingsCategoryAbout
import com.sapuseven.untis.feature.settings.fragments.SettingsCategoryAboutContributors
import com.sapuseven.untis.feature.settings.fragments.SettingsCategoryAboutLibraries
import com.sapuseven.untis.feature.settings.fragments.SettingsCategoryGeneral
import com.sapuseven.untis.feature.settings.fragments.SettingsCategoryNotifications
import com.sapuseven.untis.feature.settings.fragments.SettingsCategoryStyling
import com.sapuseven.untis.feature.settings.fragments.SettingsCategoryTimetable
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute {
	@Serializable
	data object Categories

	@Serializable
	data object General

	@Serializable
	data object Styling

	@Serializable
	data class Timetable(@StringRes val highlightTitle: Int = -1)

	@Serializable
	data object Notifications

	@Serializable
	data object About {

		@Serializable
		data object Libraries

		@Serializable
		data object Contributors
	}
}

fun NavController.navigateToSettings() {
	navigate(route = SettingsRoute)
}

fun NavGraphBuilder.settingsScreen(
	navController: NavHostController,
) {
	navigation<SettingsRoute>(startDestination = SettingsRoute.Categories) {
		composable<SettingsRoute.Categories> { entry ->
			SettingsScreen(
				onBackPressed = { navController.popBackStack() },
				onNavigate = { navController.navigate(it) },
			)
		}

		composable<SettingsRoute.General> {
			SettingsCategoryScreen(
				navController = navController,
				title = stringResource(id = R.string.feature_settings_preferences_general)
			) { viewModel ->
				SettingsCategoryGeneral(viewModel)
			}
		}

		composable<SettingsRoute.Styling> {
			SettingsCategoryScreen(
				navController = navController,
				title = stringResource(id = R.string.feature_settings_preferences_styling)
			) { viewModel ->
				SettingsCategoryStyling(viewModel)
			}
		}

		composable<SettingsRoute.Timetable> { entry ->
			val args = entry.toRoute<SettingsRoute.Timetable>()

			SettingsCategoryScreen(
				navController = navController,
				title = stringResource(id = R.string.feature_settings_preferences_timetable)
			) { viewModel ->
				SettingsCategoryTimetable(viewModel, highlightTitle = args.highlightTitle)
			}
		}

		composable<SettingsRoute.Notifications> {
			SettingsCategoryScreen(
				navController = navController,
				title = stringResource(id = R.string.feature_settings_preferences_notifications)
			) { viewModel ->
				SettingsCategoryNotifications(viewModel)
			}
		}

		composable<SettingsRoute.About> {
			SettingsCategoryScreen(
				navController = navController,
				title = stringResource(id = R.string.feature_settings_preferences_info)
			) {
				SettingsCategoryAbout(navController)
			}
		}

		composable<SettingsRoute.About.Contributors> {
			SettingsCategoryAboutContributors(navController)
		}

		composable<SettingsRoute.About.Libraries> {
			SettingsCategoryAboutLibraries(navController)
		}
	}
}

@FeatureRouteDsl
fun FeatureRoute.settingsRoute(): FeatureRouteItem = FeatureRouteItem(
	R.drawable.feature_settings_nav_icon,
	R.string.feature_settings_activity_title_settings,
	SettingsRoute
)
