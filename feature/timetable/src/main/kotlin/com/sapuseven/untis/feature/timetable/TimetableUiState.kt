package com.sapuseven.untis.feature.timetable

import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.Period
import com.sapuseven.untis.core.model.timetable.WeekViewHour
import com.sapuseven.untis.core.model.user.User
import com.sapuseven.untis.feature.timetable.weekview.WeekViewColorScheme
import com.sapuseven.untis.feature.timetable.weekview.WeekViewEvent
import com.sapuseven.untis.feature.timetable.weekview.WeekViewEventStyle
import com.sapuseven.untis.feature.timetable.weekview.WeekViewHoliday
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.Instant

data class TimetableUiState(
	val user: User,
	val userList: List<User> = emptyList(),
	val debug: Boolean = false, // TODO: Maybe should be some global state?
	val title: String = "BetterUntis",

	// Navigation (Drawer)
	val bookmarks: List<Element> = emptyList(),

	// Last update timestamp
	val currentTime: Instant,
	val lastRefresh: DateTimePeriod? = null,

	// Timetable
	val currentElement: Element? = null,

	// WeekView
	val eventStyle: WeekViewEventStyle = WeekViewEventStyle.default(),
	val colorScheme: WeekViewColorScheme = WeekViewColorScheme.default(),
	val hourList: List<WeekViewHour> = emptyList(),
	val events: Map<Int, List<WeekViewEvent<Period>>> = emptyMap(),
	val holidays: List<WeekViewHoliday> = emptyList(),
	val loading: Boolean = false,
) {
	/*fun withLoadedUser(user: User): LoginDataInputUiState = copy(
		formData = LoginData.fromUser(user),
		isSchoolNameLocked = true
	)*/
}
