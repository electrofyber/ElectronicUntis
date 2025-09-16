package com.sapuseven.untis.core.domain.timetable

import java.time.LocalDate

interface WeekLogicService {
	val weekLength: Int

	fun currentWeekStartDate(): LocalDate
}
