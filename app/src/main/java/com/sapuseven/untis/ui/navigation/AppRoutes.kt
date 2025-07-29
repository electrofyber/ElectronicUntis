package com.sapuseven.untis.ui.navigation

import androidx.annotation.StringRes
import kotlinx.serialization.Serializable

object AppRoutes {
	@Serializable
	data object Splash

	@Serializable
	data object InfoCenter {
		@Serializable
		data object Messages

		@Serializable
		data object Events

		@Serializable
		data object Absences

		@Serializable
		data object OfficeHours
	}

	@Serializable
	data object RoomFinder

	@Serializable
	data object Settings {

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
}
