package com.sapuseven.untis.core.data.timetable

import com.sapuseven.untis.core.domain.timetable.WeekLogicService
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject


class WeekLogicServiceImpl @Inject constructor(
	val clock: Clock
) : WeekLogicService {
	override val weekLength: Int
		get() = 5

	override fun currentWeekStartDate(): LocalDate {
		return now().with(DayOfWeek.MONDAY).run {
			if (plusDays(weekLength.toLong() - 1).isBefore(now()))
				plusWeeks(1)
			else this
		}
	}

	private fun now() = LocalDate.now()
}
