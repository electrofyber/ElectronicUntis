package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.api.mobile.client.AbsenceJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.ClassRegJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.MessagesJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.OfficeHoursJsonrpcApi
import com.sapuseven.untis.core.api.mobile.model.response.ExamsResult
import com.sapuseven.untis.core.api.mobile.model.response.HomeworkResult
import com.sapuseven.untis.core.api.mobile.model.response.MessagesOfDayResult
import com.sapuseven.untis.core.api.mobile.model.response.OfficeHoursResult
import com.sapuseven.untis.core.api.mobile.model.response.StudentAbsencesResult
import com.sapuseven.untis.core.data.mapper.toData
import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.domain.repository.InfoCenterRepository
import com.sapuseven.untis.core.domain.repository.MasterDataRepository
import com.sapuseven.untis.core.model.absences.Absence
import com.sapuseven.untis.core.model.messages.MessageOfDay
import com.sapuseven.untis.core.model.officehours.OfficeHour
import com.sapuseven.untis.core.model.timetable.ElementKey
import com.sapuseven.untis.core.model.timetable.Exam
import com.sapuseven.untis.core.model.timetable.Homework
import com.sapuseven.untis.core.model.user.User
import crocodile8.universal_cache.time.TimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class UntisInfoCenterRepository @Inject constructor(
	private val masterDataRepository: MasterDataRepository,
	private val messagesApi: MessagesJsonrpcApi,
	private val classRegApi: ClassRegJsonrpcApi,
	private val absenceApi: AbsenceJsonrpcApi,
	private val officeHoursApi: OfficeHoursJsonrpcApi,
	@Named("cacheDir") cacheDir: File,
	timeProvider: TimeProvider,
) : BaseCachedRepository(cacheDir, timeProvider), InfoCenterRepository {
	override fun getMessagesOfDay(
		user: User,
		day: LocalDate
	): Flow<List<MessageOfDay>> =
		cached<LocalDate, MessagesOfDayResult>("infocenter/messagesofday") {
			messagesApi.getMessagesOfDay(
				date = day,
				apiUrl = user.school.api.jsonRpc,
				user = user.credentials?.user,
				key = user.credentials?.key
			)
		}
			.invoke(day, user.id)
			.map { result ->
				result.messages.map { it.toDomain() }
			}

	override fun getExams(
		user: User,
		params: InfoCenterRepository.EventsParams
	): Flow<List<Exam>> =
		cached<InfoCenterRepository.EventsParams, ExamsResult>("infocenter/exams") {
			classRegApi.getExams(
				id = params.elementId,
				type = params.elementType.toData(),
				startDate = params.startDate,
				endDate = params.endDate,
				apiUrl = user.school.api.jsonRpc,
				user = user.credentials?.user,
				key = user.credentials?.key
			)
		}
			.invoke(params, user.id)
			.map { result ->
				result.exams.map { it.toDomain(allElements()) }
			}

	override fun getHomework(
		user: User,
		params: InfoCenterRepository.EventsParams
	): Flow<List<Homework>> =
		cached<InfoCenterRepository.EventsParams, HomeworkResult>("infocenter/homework") {
			classRegApi.getHomework(
				id = params.elementId,
				type = params.elementType.toData(),
				startDate = params.startDate,
				endDate = params.endDate,
				apiUrl = user.school.api.jsonRpc,
				user = user.credentials?.user,
				key = user.credentials?.key
			)
		}
			.invoke(params, user.id)
			.map { result ->
				result.homeWorks.map { it.toDomain(allElements()) }
			}

	override fun getOfficeHours(
		user: User,
		params: InfoCenterRepository.OfficeHoursParams
	): Flow<List<OfficeHour>> =
		cached<InfoCenterRepository.OfficeHoursParams, OfficeHoursResult>("infocenter/officehours") {
			officeHoursApi.getOfficeHours(
				klasseId = params.classId,
				startDate = params.startDate,
				apiUrl = user.school.api.jsonRpc,
				user = user.credentials?.user,
				key = user.credentials?.key
			)
		}
			.invoke(params, user.id)
			.map { result ->
				result.officeHours.map { it.toDomain(allElements()) }
			}


	override fun getAbsences(
		user: User,
		params: InfoCenterRepository.AbsencesParams
	): Flow<List<Absence>> =
		cached<InfoCenterRepository.AbsencesParams, StudentAbsencesResult>("infocenter/absences") {
			absenceApi.getStudentAbsences(
				startDate = params.startDate,
				endDate = params.endDate,
				includeExcused = params.includeExcused,
				includeUnExcused = params.includeUnExcused,
				apiUrl = user.school.api.jsonRpc,
				user = user.credentials?.user,
				key = user.credentials?.key
			)
		}
			.invoke(params, user.id)
			.map { result ->
				val students = user.element?.let { mapOf(it.id to it) } ?: emptyMap() // TODO
				result.absences.map { it.toDomain(allElements(), students) }
			}

	private suspend fun allElements() =
		masterDataRepository.getAllElements().associateBy { ElementKey(it.id, it.type) }
}
