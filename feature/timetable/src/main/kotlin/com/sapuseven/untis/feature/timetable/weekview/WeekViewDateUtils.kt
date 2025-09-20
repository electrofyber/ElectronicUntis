package com.sapuseven.untis.feature.timetable.weekview

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

/**
 * Calculates the relative page index corresponding to a date.
 * The page index for today will always be `0`.
 *
 * @param date The date to calculate the page index for
 * @param clock The clock to use for the current date
 * @param firstDayOfWeek The first day of the week on a page
 * @param weekLength The number of days displayed per page
 * @param defaultToNext Whether to return the next or the previous week if the specified [date] isn't visible
 * (e.g. for weekends when only week days are displayed)
 *
 * @return The page index relative to the page corresponding to today
 * @see startDateForPageIndex
 */
internal fun pageIndexForDate(
	date: LocalDate,
	clock: Clock = Clock.System,
	zone: TimeZone = TimeZone.currentSystemDefault(),
	firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
	weekLength: Int = 5,
	defaultToNext: Boolean = true
): Int {
	val today = clock.todayIn(zone)
	val weekStart = date.weekStart(firstDayOfWeek)
	val todayWeekStart = today.weekStart(firstDayOfWeek)

	val weeks = (weekStart - todayWeekStart).days / 7
	val dayOffsetFromWeekStart = (date - weekStart).days
	val additionalWeeks = if (dayOffsetFromWeekStart >= weekLength && defaultToNext) 1 else 0

	return weeks + additionalWeeks
}

/**
 * Calculates the start date for a specific page.
 *
 * @param pageIndex The page index relative to today's page
 * @param clock The clock to use for the current date
 * @param firstDayOfWeek The first day of the week on a page
 * @param weekLength The number of days displayed per page
 * @param defaultToNext Whether to return the next or the previous week if today isn't visible
 * (e.g. on weekends when only week days are displayed)
 *
 * @return The date of the first visible day on the page specified by [pageIndex]
 * @see pageIndexForDate
 */
internal fun startDateForPageIndex(
	pageIndex: Int,
	clock: Clock = Clock.System,
	zone: TimeZone = TimeZone.currentSystemDefault(),
	firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
	weekLength: Int = 5,
	defaultToNext: Boolean = true
): LocalDate {
	val today = clock.todayIn(zone)
	val firstVisible = today.weekStart(firstDayOfWeek)
	val additionalWeeks = if ((today - firstVisible).days >= weekLength && defaultToNext) 1 else 0

	return firstVisible.plus(pageIndex + additionalWeeks, DateTimeUnit.WEEK)
}

private fun LocalDate.weekStart(firstDayOfWeek: DayOfWeek): LocalDate {
	val currentDow = dayOfWeek
	val daysToSubtract = (currentDow.isoDayNumber - firstDayOfWeek.isoDayNumber + 7) % 7
	return minus(daysToSubtract, DateTimeUnit.DAY)
}

internal class DateIterator(
	private var startDate: LocalDate,
	private val endDateInclusive: LocalDate,
	private val stepDays: Long
) : Iterator<LocalDate> {
	override fun hasNext(): Boolean = startDate <= endDateInclusive
	override fun next(): LocalDate = startDate.also {
		startDate = startDate.plus(stepDays, DateTimeUnit.DAY)
	}
}

internal class DateProgression(
	override val start: LocalDate,
	override val endInclusive: LocalDate,
	val stepDays: Long = 1
) : Iterable<LocalDate>, ClosedRange<LocalDate> {

	override fun iterator(): Iterator<LocalDate> =
		DateIterator(start, endInclusive, stepDays)

	infix fun step(days: Long) = DateProgression(start, endInclusive, days)
}

internal operator fun LocalDate.rangeTo(other: LocalDate) =
	DateProgression(this, other)
