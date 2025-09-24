package com.sapuseven.untis.core.data.service

import com.sapuseven.untis.core.domain.timetable.WeekLogicService
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import javax.inject.Inject

class WeekLogicServiceImpl @Inject constructor(
	val clock: Clock,
	val zone: TimeZone,
) : WeekLogicService {
	override val weekLength: Int
		get() = 5

	override val firstDayOfWeek: DayOfWeek
		get() = DayOfWeek.MONDAY

	override fun currentWeekStartDate(): java.time.LocalDate {
		return now().with(java.time.DayOfWeek.MONDAY).run {
			if (plusDays(weekLength.toLong() - 1).isBefore(now()))
				plusWeeks(1)
			else this
		}
	}

	override fun pageIndexForDate(
		date: LocalDate,
		defaultToNext: Boolean,
	): Int {
		val today = clock.todayIn(zone)
		val weekStart = date.weekStart(firstDayOfWeek)
		val todayWeekStart = today.weekStart(firstDayOfWeek)

		val weeks = (weekStart - todayWeekStart).days / 7
		val dayOffsetFromWeekStart = (date - weekStart).days
		val additionalWeeks = if (dayOffsetFromWeekStart >= weekLength && defaultToNext) 1 else 0

		return weeks + additionalWeeks
	}

	override fun startDateForPageIndex(
		pageIndex: Int,
		defaultToNext: Boolean,
	): LocalDate {
		val today = clock.todayIn(zone)
		val firstVisible = today.weekStart(firstDayOfWeek)
		val additionalWeeks = if ((today - firstVisible).days >= weekLength && defaultToNext) 1 else 0

		return firstVisible.plus(pageIndex + additionalWeeks, DateTimeUnit.WEEK)
	}

	private fun now() = clock.now().toLocalDateTime(zone).toJavaLocalDateTime().toLocalDate()

	private fun LocalDate.weekStart(firstDayOfWeek: DayOfWeek): LocalDate {
		val currentDow = dayOfWeek
		val daysToSubtract = (currentDow.isoDayNumber - firstDayOfWeek.isoDayNumber + 7) % 7
		return minus(daysToSubtract, DateTimeUnit.DAY)
	}
}
