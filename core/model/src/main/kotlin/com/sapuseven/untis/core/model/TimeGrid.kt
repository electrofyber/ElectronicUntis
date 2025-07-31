package com.sapuseven.untis.core.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class TimeGrid(
	val days: List<TimeGridDay>
)

@Serializable
data class TimeGridDay(
	val dayOfWeek: DayOfWeek,
	val units: List<TimeGridUnit>
)

@Serializable
data class TimeGridUnit(
	val label: String,
	val startTime: LocalTime,
	val endTime: LocalTime
)
