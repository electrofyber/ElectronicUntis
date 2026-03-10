package com.sapuseven.untis.core.domain.repository

import com.sapuseven.untis.core.model.absences.Absence
import com.sapuseven.untis.core.model.absences.Excuse
import com.sapuseven.untis.core.model.messages.MessageOfDay
import com.sapuseven.untis.core.model.officehours.OfficeHour
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.model.timetable.Exam
import com.sapuseven.untis.core.model.timetable.Homework
import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface InfoCenterRepository {
	fun getMessagesOfDay(
		user: User,
		day: LocalDate
	): Flow<List<MessageOfDay>>

	fun getExams(
		user: User,
		params: EventsParams,
	): Flow<List<Exam>>

	fun getHomework(
		user: User,
		params: EventsParams,
	): Flow<List<Homework>>

	fun getAbsences(
		user: User,
		params: AbsencesParams,
	): Flow<List<Absence>>

	fun getExcuses(
		user: User,
	): Flow<List<Excuse>>

	fun getOfficeHours(
		user: User,
		params: OfficeHoursParams,
	): Flow<List<OfficeHour>>

	data class EventsParams(
		val elementId: Long,
		val elementType: ElementType,
		val startDate: LocalDate,
		val endDate: LocalDate
	)

	data class AbsencesParams(
		val startDate: LocalDate,
		val endDate: LocalDate,
		val includeExcused: Boolean = true,
		val includeUnExcused: Boolean = true
	)

	data class OfficeHoursParams(
		val classId: Long,
		val startDate: LocalDate
	)
}
