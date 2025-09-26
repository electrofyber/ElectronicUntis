package com.sapuseven.untis.core.domain.timetable

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

interface WeekLogicService {
	val weekLength: Int

	val firstDayOfWeek: DayOfWeek

	fun currentWeekStartDate(): java.time.LocalDate

	/**
	 * Calculates the relative page index corresponding to a date.
	 * The page index for today will always be `0`.
	 *
	 * @param date The date to calculate the page index for
	 * @param defaultToNext Whether to return the next or the previous week if the specified [date] isn't visible
	 * (e.g. for weekends when only week days are displayed)
	 *
	 * @return The page index relative to the page corresponding to today
	 * @see startDateForPageIndex
	 */
	fun pageIndexForDate(
		date: LocalDate,
		defaultToNext: Boolean = true
	): Int

	/**
	 * Calculates the start date for a specific page.
	 *
	 * @param pageIndex The page index relative to today's page
	 * @param defaultToNext Whether to return the next or the previous week if today isn't visible
	 * (e.g. on weekends when only week days are displayed)
	 *
	 * @return The date of the first visible day on the page specified by [pageIndex]
	 * @see pageIndexForDate
	 */
	fun startDateForPageIndex(
		pageIndex: Int,
		defaultToNext: Boolean = true
	): LocalDate

	/**
	 * Combines `startDateForPageIndex` and `weekLength` to provide a pair of start and end date for the week of a given page.
	 *
	 * @param pageIndex The page index relative to today's page
	 * @param defaultToNext Whether to return the next or the previous week if today isn't visible
	 * (e.g. on weekends when only week days are displayed)
	 *
	 * @return A pair of startDate and endDate of the requested page
	 * @see startDateForPageIndex
	 * @see weekLength
	 */
	fun dateRangeForPageIndex(
		pageIndex: Int,
		defaultToNext: Boolean = true
	): Pair<LocalDate, LocalDate> = startDateForPageIndex(pageIndex, defaultToNext).let { startDate ->
		startDate to startDate.plus(weekLength, DateTimeUnit.DAY)
	}
}
