package com.sapuseven.untis.feature.roomfinder.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.sapuseven.untis.core.domain.navigation.FeatureRoute
import com.sapuseven.untis.core.domain.navigation.FeatureRouteDsl
import com.sapuseven.untis.core.domain.navigation.FeatureRouteItem
import com.sapuseven.untis.feature.roomfinder.R
import com.sapuseven.untis.feature.roomfinder.RoomFinderScreen
import kotlinx.serialization.Serializable

@Serializable
data object RoomFinderRoute

fun NavGraphBuilder.roomFinderScreen(
	navController: NavHostController,
	onRoomClick: (id: Long) -> Unit,
) {
	composable<RoomFinderRoute>(
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
	) { entry ->
		RoomFinderScreen(
			onBackClick = navController::popBackStack,
			onRoomClick = onRoomClick
		)
	}
}

@FeatureRouteDsl
fun FeatureRoute.roomFinderRoute(): FeatureRouteItem = FeatureRouteItem(
	R.drawable.feature_roomfinder_nav_icon,
	R.string.feature_roomfinder_title,
	RoomFinderRoute
)
