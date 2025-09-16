package com.sapuseven.untis.feature.timetable.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.sapuseven.untis.core.model.Element
import com.sapuseven.untis.core.model.ElementType
import com.sapuseven.untis.feature.timetable.TimetableScreen
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf


@Serializable
data class TimetableRoute(
	val id: Long? = null,
	val type: ElementType? = null,
) {
	constructor(element: Element?) : this(element?.id, element?.type)

	private companion object
}

fun NavController.navigateToTimetable(
	elementId: Long? = null,
	elementType: ElementType? = null,
	navOptions: NavOptionsBuilder.() -> Unit = {}
) {
	navigate(route = TimetableRoute(elementId, elementType)) {
		navOptions()
	}
}

fun NavGraphBuilder.timetableScreen(
	onElementClicked: (id: Long?, type: ElementType?) -> Unit,
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
		TimetableScreen(
			onElementClicked = onElementClicked,
			onUserEdit = onUserEdit,
		)
	}
}
