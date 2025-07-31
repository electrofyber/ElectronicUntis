package com.sapuseven.untis.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.sapuseven.untis.feature.login.navigation.loginScreen

@Composable
fun AppNavHost(
	modifier: Modifier = Modifier,
	navController: NavHostController = rememberNavController(),
	startDestination: Any // = AppRoutes.Splash,
) {
	NavHost(
		modifier = modifier,
		navController = navController,
		startDestination = startDestination,
	) {
		loginScreen(
			navController = navController,
			onComplete = {
				//navController.navigateToTimetable { popUpTo(0) }
			}
		)

		/*timetableScreen(
			onUserEdit = navController::navigateToLoginDataInput
		)*/

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
		}

		navigation<AppRoutes.Settings>(
			startDestination = AppRoutes.Settings.Categories,
			enterTransition = { materialSharedAxisXIn(true, 100) },
			exitTransition = { materialSharedAxisXOut(true, 100) },
			popEnterTransition = { materialSharedAxisXIn(false, 100) },
			popExitTransition = { materialSharedAxisXOut(false, 100) }
		) {
			settingsNav(navController = navController)
		}*/
	}
}
