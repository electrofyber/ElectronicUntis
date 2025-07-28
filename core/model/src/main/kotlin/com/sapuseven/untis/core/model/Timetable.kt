package com.sapuseven.untis.core.model

import java.time.Instant
import java.time.LocalDate

/**
 * A timetable represents the timetable for a specific time period, usually a week.
 *
 * It can be a range of one or more days, containing several [Period]s.
 *
 * @property startDate The start date of the timetable.
 * @property endDate The end date of the timetable.
 * @property timestamp The timestamp of when the periods
 * @property periods A list of periods within the timetable.
 * @see Period
 * @see Element
 */
data class Timetable(
	val startDate: LocalDate,
	val endDate: LocalDate,
	val timestamp: Instant,
	val periods: List<Period>,
)


