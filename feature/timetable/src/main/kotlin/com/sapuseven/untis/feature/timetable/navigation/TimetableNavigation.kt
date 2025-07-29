package com.sapuseven.untis.feature.timetable.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sapuseven.untis.core.model.Element
import com.sapuseven.untis.core.model.ElementType
import com.sapuseven.untis.feature.timetable.TimetableScreen
import com.sapuseven.untis.feature.timetable.TimetableViewModel
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf


@Serializable
data class TimetableRoute(
	val id: Long? = null,
	val type: ElementType? = null,
) {
	constructor(element: Element?) : this(element?.id, element?.type)
}

fun NavController.navigateToTimetable(
	elementId: Long,
	elementType: ElementType,
	navOptions: NavOptionsBuilder.() -> Unit = {}
) {
	navigate(route = TimetableRoute(elementId, elementType)) {
		navOptions()
	}
}

fun NavGraphBuilder.timetableScreen(
	onUserEdit: (Long?) -> Unit,
) {
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
		val id = entry.toRoute<TimetableRoute>().id
		val type = entry.toRoute<TimetableRoute>().type
		TimetableScreen(
			onUserEdit = onUserEdit,
			viewModel = hiltViewModel<TimetableViewModel, TimetableViewModel.Factory>(
				key = (type.toString() + id.toString()),
			) { factory ->
				factory.create(id, type)
			},
		)
	}
}
