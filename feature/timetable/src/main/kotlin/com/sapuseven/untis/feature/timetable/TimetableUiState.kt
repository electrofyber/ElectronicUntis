package com.sapuseven.untis.feature.timetable

import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.user.User
import java.time.Duration
import java.time.LocalDateTime

data class TimetableUiState(
	val user: User,
	val userList: List<User> = emptyList(),
	val debug: Boolean = false, // TODO: Maybe should be some global state?
	val title: String = "BetterUntis",

	// Navigation (Drawer)
	val bookmarks: List<Element> = emptyList(),

	// Last update timestamp
	val currentTime: LocalDateTime,
	val lastRefresh: Duration? = null,

	// Timetable
	val currentElement: ElementEntity? = null,
) {
	/*fun withLoadedUser(user: User): LoginDataInputUiState = copy(
		formData = LoginData.fromUser(user),
		isSchoolNameLocked = true
	)*/
}
