package com.sapuseven.untis.core.model.timetable

import kotlinx.datetime.LocalTime

data class WeekViewHour(
	val startTime: LocalTime,
	val endTime: LocalTime,
	val label: String
)
