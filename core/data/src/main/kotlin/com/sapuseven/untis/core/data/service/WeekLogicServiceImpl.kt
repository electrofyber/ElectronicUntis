package com.sapuseven.untis.core.data.service

import com.sapuseven.untis.core.domain.timetable.WeekLogicService
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject


class WeekLogicServiceImpl @Inject constructor(
	val clock: Clock,
	val timeZone: TimeZone,
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

	private fun now() = clock.now().toLocalDateTime(timeZone).toJavaLocalDateTime().toLocalDate()
}
