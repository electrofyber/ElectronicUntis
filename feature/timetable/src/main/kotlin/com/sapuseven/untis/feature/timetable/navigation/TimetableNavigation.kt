@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sapuseven.untis.feature.timetable.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.sapuseven.untis.core.domain.navigation.FeatureRoute
import com.sapuseven.untis.core.domain.navigation.FeatureRouteItem
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.feature.timetable.TimetableScreen
import com.sapuseven.untis.feature.timetable.TimetableViewModel
import com.sapuseven.untis.feature.timetable.details.PeriodDetailsScreen
import com.sapuseven.untis.feature.timetable.details.PeriodDetailsViewModel
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
data object TimetableBaseRoute

@Serializable
data class TimetableRoute(
	val id: Long? = null,
	val type: ElementType? = null,
) {
	private companion object
}

@Serializable
data class PeriodDetailsRoute(
	val id: Long,
	val type: ElementType,
	val page: Int,
	val periodIds: List<Long>,
	val initialPeriod: Int,
) {
	private companion object
}

fun NavController.navigateToTimetable(
	elementId: Long? = null,
	elementType: ElementType? = null,
	navOptions: NavOptionsBuilder.() -> Unit = {}
) {
	navigate(route = TimetableRoute(elementId, elementType)) {
		popUpTo<TimetableRoute>() // When navigating to a timetable, only keep the last visited timetable
		navOptions()
	}
}

fun NavController.navigateToPeriodDetails(
	id: Long,
	type: ElementType,
	page: Int,
	periodIds: List<Long>,
	initialPeriod: Int
) {
	navigate(route = PeriodDetailsRoute(id, type, page, periodIds, initialPeriod))
}

fun NavGraphBuilder.timetableScreen(
	navController: NavHostController,
	onElementClick: (id: Long?, type: ElementType?) -> Unit,
	onUserEdit: (Long?) -> Unit,
	onPeriodDetails: (id: Long, type: ElementType, timetablePage: Int, periodIds: List<Long>, initialPeriod: Int) -> Unit,
	sharedTransitionScope: SharedTransitionScope,
	featureRoutes: @Composable FeatureRoute.() -> List<FeatureRouteItem>,
	periodDetailsDestination: NavGraphBuilder.() -> Unit,
) {
	navigation<TimetableBaseRoute>(startDestination = TimetableRoute()) {
		composable<TimetableRoute>(
			typeMap = mapOf(typeOf<ElementType>() to NavType.EnumType(ElementType::class.java)),
			enterTransition = {
				fadeIn(animationSpec = tween(500))
			},
			exitTransition = {
				fadeOut(animationSpec = tween(500))
			},
			popEnterTransition = {
				fadeIn(animationSpec = tween(500))
			},
			popExitTransition = {
				fadeOut(animationSpec = tween(500))
			},
		) { entry ->
			val (id, type) = entry.toRoute<TimetableRoute>()
			val viewModel = hiltViewModel<TimetableViewModel, TimetableViewModel.Factory>(
				key = "timetable-$type-$id"
			) { it.create(id, type) }

			TimetableScreen(
				viewModel = viewModel,
				featureRoutes = featureRoutes,
				onNavigate = navController::navigate,
				onUserEdit = onUserEdit,
				onElementClick = onElementClick,
				onPeriodDetails = onPeriodDetails,
				sharedTransitionScope = sharedTransitionScope,
				animatedVisibilityScope = this
			)
		}
		periodDetailsDestination()
	}
}

fun NavGraphBuilder.periodDetailsScreen(
	onBackClick: () -> Unit,
	onElementClick: (id: Long?, type: ElementType?) -> Unit,
	sharedTransitionScope: SharedTransitionScope,
) {
	composable<PeriodDetailsRoute>(
		typeMap = mapOf(typeOf<ElementType>() to NavType.EnumType(ElementType::class.java)),
		enterTransition = { EnterTransition.None },
		exitTransition  = { ExitTransition.None },
		popEnterTransition = { EnterTransition.None },
		popExitTransition  = { ExitTransition.None }
	) { entry ->
		val route = entry.toRoute<PeriodDetailsRoute>()
		val viewModel = hiltViewModel<PeriodDetailsViewModel, PeriodDetailsViewModel.Factory>(
			key = "period-${route.type}-${route.id}-${route.page}-${route.periodIds.hashCode()}"
		) { factory ->
			factory.create(
				id = route.id,
				type = route.type,
				page = route.page,
				periodIds = route.periodIds,
				initialPeriod = route.initialPeriod
			)
		}

		PeriodDetailsScreen(
			viewModel = viewModel,
			sharedTransitionScope = sharedTransitionScope,
			animatedVisibilityScope = this,
			initialPeriodId = route.periodIds[route.initialPeriod],
			onBackClick = onBackClick,
			onElementClick = onElementClick
		)
	}
}
