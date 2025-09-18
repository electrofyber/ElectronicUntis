package com.sapuseven.untis.core.domain.repository

import com.sapuseven.untis.core.domain.cache.FromCache
import com.sapuseven.untis.core.model.absences.Absence
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.model.timetable.Period
import com.sapuseven.untis.core.model.timetable.PeriodDetails
import com.sapuseven.untis.core.model.timetable.Timetable
import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime


interface TimetableRepository {
	fun getTimetable(
		user: User,
		params: TimetableParams,
		fromCache: FromCache
	): Flow<Timetable>

	suspend fun getPeriodData(
		user: User,
		periods: Set<Period>
	): Result<Map<Long, PeriodDetails>>

	suspend fun postLessonTopic(
		user: User,
		periodId: Long,
		lessonTopic: String
	): Result<Boolean>

	suspend fun postAbsence(
		user: User,
		periodId: Long,
		studentId: Long,
		startTime: LocalTime,
		endTime: LocalTime
	): Result<List<Absence>>

	suspend fun deleteAbsence(
		user: User,
		absenceId: Long
	): Result<Boolean>

	suspend fun postAbsencesChecked(
		user: User,
		periodIds: Set<Long>
	): Result<Unit>

	data class TimetableParams(
		val elementId: Long,
		val elementType: ElementType,
		val startDate: LocalDate,
		val endDate: LocalDate
	)
}
