@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sapuseven.untis.feature.timetable.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.sapuseven.untis.core.domain.navigation.FeatureRoute
import com.sapuseven.untis.core.domain.navigation.FeatureRouteItem
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.feature.timetable.TimetableScreen
import com.sapuseven.untis.feature.timetable.TimetableViewModel
import com.sapuseven.untis.feature.timetable.details.PeriodDetailsScreen
import com.sapuseven.untis.feature.timetable.details.PeriodDetailsViewModel
import com.sapuseven.untis.feature.timetable.user.UserDeleteDialog
import com.sapuseven.untis.feature.timetable.user.UserListDialogContent
import com.sapuseven.untis.feature.timetable.user.UserListViewModel
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

@Serializable
data object UserListRoute

@Serializable
data class UserDeleteRoute(
	val id: Long
)

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

fun NavController.navigateToUserList() {
	navigate(route = UserListRoute)
}

fun NavController.navigateToUserDelete(id: Long) {
	navigate(route = UserDeleteRoute(id))
}

fun NavGraphBuilder.timetableScreen(
	navController: NavHostController,
	onElementClick: (id: Long?, type: ElementType?) -> Unit,
	onPeriodDetails: (id: Long, type: ElementType, timetablePage: Int, periodIds: List<Long>, initialPeriod: Int) -> Unit,
	sharedTransitionScope: SharedTransitionScope,
	featureRoutes: @Composable FeatureRoute.() -> List<FeatureRouteItem>,
	userListDestination: NavGraphBuilder.() -> Unit,
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
				onUserListClick = navController::navigateToUserList,
				onElementClick = onElementClick,
				onPeriodDetails = onPeriodDetails,
				sharedTransitionScope = sharedTransitionScope,
				animatedVisibilityScope = this
			)
		}

		userListDestination()
		periodDetailsDestination()
	}
}

fun NavGraphBuilder.userListScreen(
	onBackClick: () -> Unit,
	onUserEdit: (Long?) -> Unit,
	onUserDelete: (Long) -> Unit,
) {
	composable<UserListRoute>(
		typeMap = mapOf(typeOf<ElementType>() to NavType.EnumType(ElementType::class.java)),
		enterTransition = {
			fadeIn(tween(250)) + slideInVertically { it / 2 }
		},
		exitTransition = {
			fadeOut(tween(200)) + slideOutHorizontally { -it / 2 }
		},
		popEnterTransition = {
			fadeIn(tween(250)) + slideInHorizontally { -it / 2 }
		},
		popExitTransition = {
			fadeOut(tween(200)) + slideOutVertically { it / 2 }
		},
	) {
		UserListDialogContent(
			onBackClick = onBackClick,
			onUserEdit = onUserEdit,
			onUserDelete = onUserDelete,
		)
	}

	dialog<UserDeleteRoute> { backStackEntry ->
		val route = backStackEntry.toRoute<UserDeleteRoute>()
		val viewModel: UserListViewModel = hiltViewModel(backStackEntry)

		UserDeleteDialog(
			onConfirm = {
				viewModel.deleteUser(route.id)
				onBackClick()
			},
			onDismiss = {
				onBackClick()
			}
		)
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
	) { backStackEntry ->
		val route = backStackEntry.toRoute<PeriodDetailsRoute>()
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
