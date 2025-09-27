package com.sapuseven.untis.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.sapuseven.untis.feature.login.navigation.loginScreen
import com.sapuseven.untis.feature.login.navigation.navigateToLoginDataInput
import com.sapuseven.untis.feature.settings.navigation.settingsRoute
import com.sapuseven.untis.feature.settings.navigation.settingsScreen
import com.sapuseven.untis.feature.timetable.navigation.navigateToPeriodDetails
import com.sapuseven.untis.feature.timetable.navigation.navigateToTimetable
import com.sapuseven.untis.feature.timetable.navigation.periodDetailsScreen
import com.sapuseven.untis.feature.timetable.navigation.timetableScreen

@Composable
fun AppNavHost(
	modifier: Modifier = Modifier,
	navController: NavHostController = rememberNavController(),
	startDestination: Any
) {
	NavHost(
		modifier = modifier,
		navController = navController,
		startDestination = startDestination,
	) {
		loginScreen(
			navController = navController,
			onComplete = {
				navController.navigateToTimetable { popUpTo(0) }
			}
		)

		timetableScreen(
			navController = navController,
			onElementClick = navController::navigateToTimetable,
			onUserEdit = navController::navigateToLoginDataInput,
			onPeriodDetails = navController::navigateToPeriodDetails,
			featureRoutes = { listOf(
				//route(InfoCenterRoute),
				//route(RoomFinderRoute),
				settingsRoute(),
			)}
		) {
			periodDetailsScreen(
				onBackClick = navController::popBackStack,
				onElementClick = navController::navigateToTimetable,
			)
		}

		settingsScreen(
			navController = navController
		)

		/*infoCenterScreen(
			onBackClick = navController::popBackStack
		)*/

		/*composable<AppRoutes.InfoCenter>(
			enterTransition = {
				slideInVertically() { it / 2 } + fadeIn()
			},
			exitTransition = {
				slideOutVertically() { it / 2 } + fadeOut()
			},
			popEnterTransition = {
				slideInVertically() { it / 2 } + fadeIn()
			},
			popExitTransition = {
				slideOutVertically() { it / 2 } + fadeOut()
			},
		) {
			InfoCenter()
		}

		composable<AppRoutes.RoomFinder>(
			enterTransition = {
				slideInVertically() { it / 2 } + fadeIn()
			},
			exitTransition = {
				slideOutVertically() { it / 2 } + fadeOut()
			},
			popEnterTransition = {
				slideInVertically() { it / 2 } + fadeIn()
			},
			popExitTransition = {
				slideOutVertically() { it / 2 } + fadeOut()
			},
		) {
			RoomFinder()
		}*/

		settingsScreen(
			navController = navController
		)
	}
}
