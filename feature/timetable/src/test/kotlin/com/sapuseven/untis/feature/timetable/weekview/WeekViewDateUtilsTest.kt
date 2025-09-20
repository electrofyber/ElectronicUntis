package com.sapuseven.untis.feature.timetable.weekview

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test
import kotlin.test.assertEquals

internal class WeekViewDateUtilsTest {
	var mondayClock: Clock = object : Clock {
		override fun now() = Instant.parse("2023-06-19T00:00:00Z")
	}
	var sundayClock: Clock = object : Clock {
		override fun now() = Instant.parse("2023-06-18T00:00:00Z")
	}
	var zone: TimeZone = TimeZone.UTC

	@Test
	fun pageIndexForDate_dateIsWeekday() {
		val nowDate = mondayClock.todayIn(zone)

		assertEquals(0, pageIndexForDate(nowDate, mondayClock)) // Monday
		assertEquals(0, pageIndexForDate(nowDate.plus(1, DateTimeUnit.DAY), mondayClock)) // Tuesday
		assertEquals(1, pageIndexForDate(nowDate.plus(1, DateTimeUnit.WEEK), mondayClock)) // next Monday
		assertEquals(2, pageIndexForDate(nowDate.plus(2, DateTimeUnit.WEEK), mondayClock)) // Monday in 2 weeks
		assertEquals(-1, pageIndexForDate(nowDate.minus(1, DateTimeUnit.WEEK), mondayClock)) // last Monday
		assertEquals(-2, pageIndexForDate(nowDate.minus(2, DateTimeUnit.WEEK), mondayClock)) // Monday 2 weeks ago
	}

	@Test
	fun pageIndexForDate_dateIsWeekend() {
		val nowDate = mondayClock.todayIn(zone)

		assertEquals(0, pageIndexForDate(nowDate.minus(1, DateTimeUnit.DAY), mondayClock)) // last Sunday
		assertEquals(1, pageIndexForDate(nowDate.plus(5, DateTimeUnit.DAY), mondayClock)) // Saturday

		assertEquals(-1, pageIndexForDate(nowDate.minus(1, DateTimeUnit.DAY), mondayClock, defaultToNext = false)) // last Sunday
		assertEquals(0, pageIndexForDate(nowDate.plus(5, DateTimeUnit.DAY), mondayClock, defaultToNext = false)) // Saturday
	}

	@Test
	fun pageIndexForDate_weekLength_dateIsWeekend() {
		val nowDate = mondayClock.todayIn(zone)

		assertEquals(0, pageIndexForDate(nowDate.minus(3, DateTimeUnit.DAY), mondayClock, weekLength = 3)) // last Friday
		assertEquals(1, pageIndexForDate(nowDate.plus(3, DateTimeUnit.DAY), mondayClock, weekLength = 3)) // Thursday
	}

	@Test
	fun startDateForPageIndex_defaultParameters() {
		assertEquals(LocalDate(2023, 6, 19), startDateForPageIndex(0, mondayClock))
		assertEquals(LocalDate(2023, 6, 26), startDateForPageIndex(1, mondayClock))
		assertEquals(LocalDate(2023, 6, 12), startDateForPageIndex(-1, mondayClock))
	}

	@Test
	fun startDateForPageIndex_todayIsWeekend() {
		assertEquals(LocalDate(2023, 6, 19), startDateForPageIndex(0, sundayClock))
		assertEquals(LocalDate(2023, 6, 26), startDateForPageIndex(1, sundayClock))
		assertEquals(LocalDate(2023, 6, 12), startDateForPageIndex(-1, sundayClock))
	}

	@ParameterizedTest
	@ValueSource(ints = [-5, -1, 0, 1, 2, 5, 100])
	fun startDateForPageIndex_pageIndexForDate_isIsomorphic(pageIndex: Int) {
		assertEquals(pageIndex, pageIndexForDate(startDateForPageIndex(pageIndex)))
	}
}
