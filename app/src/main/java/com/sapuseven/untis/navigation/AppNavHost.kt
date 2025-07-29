package com.sapuseven.untis.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sapuseven.untis.feature.login.navigation.loginScreen
import com.sapuseven.untis.feature.login.navigation.navigateToLoginDataInput
import com.sapuseven.untis.feature.timetable.navigation.timetableScreen
import com.sapuseven.untis.ui.navigation.AppNavigator
import com.sapuseven.untis.ui.navigation.AppRoutes
import com.sapuseven.untis.ui.pages.splash.Splash

@Composable
fun AppNavHost(
	navigator: AppNavigator,
	modifier: Modifier = Modifier,
	navController: NavHostController = rememberNavController(),
	startDestination: Any = AppRoutes.Splash,
) {
	LaunchedEffect(navigator) {
		navigator.navActions.collect { action ->
			action?.let {
				navController.navigate(it.destination, it.navOptions)
			} ?: navController.popBackStack()
		}
	}

	val navBackStackEntry by navController.currentBackStackEntryAsState()
	LaunchedEffect(navBackStackEntry) {
		var route = navController.currentBackStackEntry?.destination?.route
		navController.currentBackStackEntry?.arguments?.keySet()

		navController.currentBackStackEntry?.arguments?.let {
			navController.currentBackStackEntry?.arguments?.keySet()?.forEach { key ->
				val value = navController.currentBackStackEntry?.arguments?.get(key)?.toString() ?: "[null]"
				route = route?.replaceFirst("{$key}", value)
			}
		}
		Log.d("AppNavigation", route ?: "/")
	}

	NavHost(
		modifier = modifier,
		navController = navController,
		startDestination = startDestination,
	) {
		composable<AppRoutes.Splash> { Splash() }

		loginScreen(
			onBackClick = navController::popBackStack,
			onDemoClick = { navController.navigateToLoginDataInput(demoLogin = true) },
			onManualDataInputClick = { navController.navigateToLoginDataInput(null) }
		)

		timetableScreen(
			onUserEdit = navController::navigateToLoginDataInput
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
