package com.sapuseven.untis.core.domain.repository

import com.sapuseven.untis.core.domain.cache.FromCache
import com.sapuseven.untis.core.model.absences.Absence
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.model.timetable.Period
import com.sapuseven.untis.core.model.timetable.PeriodDetails
import com.sapuseven.untis.core.model.timetable.Timetable
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus


interface TimetableRepository {
	suspend fun getTimetable(params: TimetableParams, fromCache: FromCache): Flow<Timetable>

	suspend fun getPeriodData(periods: Set<Period>): Result<Map<Long, PeriodDetails>>

	suspend fun postLessonTopic(periodId: Long, lessonTopic: String): Result<Boolean>

	suspend fun postAbsence(
		periodId: Long,
		studentId: Long,
		startTime: LocalTime,
		endTime: LocalTime
	): Result<List<Absence>>

	suspend fun deleteAbsence(absenceId: Long): Result<Boolean>

	suspend fun postAbsencesChecked(periodIds: Set<Long>): Result<Unit>

	data class TimetableParams(
		val elementId: Long,
		val elementType: ElementType,
		val startDate: LocalDate,
		val endDate: LocalDate = startDate.plus(DatePeriod(days = 5 /*TODO*/))
	)
}
