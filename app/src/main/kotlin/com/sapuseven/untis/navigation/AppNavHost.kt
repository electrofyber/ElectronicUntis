package com.sapuseven.untis.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.sapuseven.untis.feature.timetable.navigation.navigateToUserDelete
import com.sapuseven.untis.feature.timetable.navigation.periodDetailsScreen
import com.sapuseven.untis.feature.timetable.navigation.timetableScreen
import com.sapuseven.untis.feature.timetable.navigation.userListScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(
	modifier: Modifier = Modifier,
	navController: NavHostController = rememberNavController(),
	startDestination: Any
) {
	SharedTransitionLayout {
		NavHost(
			modifier = modifier,
			navController = navController,
			startDestination = startDestination,
			enterTransition = {
				fadeIn(tween(250)) + slideInHorizontally { it / 2 }
			},
			exitTransition = {
				fadeOut(tween(200)) + slideOutHorizontally { -it / 2 }
			},
			popEnterTransition = {
				fadeIn(tween(250)) + slideInHorizontally { -it / 2 }
			},
			popExitTransition = {
				fadeOut(tween(200)) + slideOutHorizontally { it / 2 }
			},
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
				onPeriodDetails = navController::navigateToPeriodDetails,
				sharedTransitionScope = this@SharedTransitionLayout,
				featureRoutes = {
					listOf(
						//route(InfoCenterRoute),
						//route(RoomFinderRoute),
						settingsRoute(),
					)
				},
				userListDestination = {
					userListScreen(
						onBackClick = navController::popBackStack,
						onUserEdit = navController::navigateToLoginDataInput,
						onUserDelete = navController::navigateToUserDelete,
					)
				},
				periodDetailsDestination = {
					periodDetailsScreen(
						onBackClick = navController::popBackStack,
						onElementClick = navController::navigateToTimetable,
						sharedTransitionScope = this@SharedTransitionLayout,
					)
				}
			)

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
}
