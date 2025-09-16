package com.sapuseven.untis.feature.timetable

import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.Period
import com.sapuseven.untis.core.model.user.User
import com.sapuseven.untis.feature.timetable.weekview.Event
import com.sapuseven.untis.feature.timetable.weekview.Holiday
import com.sapuseven.untis.feature.timetable.weekview.WeekViewColorScheme
import com.sapuseven.untis.feature.timetable.weekview.WeekViewEventStyle
import com.sapuseven.untis.feature.timetable.weekview.WeekViewHour
import java.time.Duration
import java.time.LocalDate
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
	val currentElement: Element? = null,

	// WeekView
	val eventStyle: WeekViewEventStyle = WeekViewEventStyle.default(),
	val colorScheme: WeekViewColorScheme = WeekViewColorScheme.default(),
	val hourList: List<WeekViewHour> = emptyList(),
	val events: Map<LocalDate, List<Event<Period>>> = emptyMap(),
	val holidays: List<Holiday> = emptyList(),
	val loading: Boolean = false,
) {
	/*fun withLoadedUser(user: User): LoginDataInputUiState = copy(
		formData = LoginData.fromUser(user),
		isSchoolNameLocked = true
	)*/
}
