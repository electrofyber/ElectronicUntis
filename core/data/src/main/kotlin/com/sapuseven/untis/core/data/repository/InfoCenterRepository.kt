package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.api.client.AbsenceApi
import com.sapuseven.untis.core.api.client.ClassRegApi
import com.sapuseven.untis.core.api.client.MessagesApi
import com.sapuseven.untis.core.api.client.OfficeHoursApi
import com.sapuseven.untis.core.api.model.response.ExamsResult
import com.sapuseven.untis.core.api.model.response.HomeworkResult
import com.sapuseven.untis.core.api.model.response.MessagesOfDayResult
import com.sapuseven.untis.core.api.model.response.OfficeHoursResult
import com.sapuseven.untis.core.api.model.response.StudentAbsencesResult
import com.sapuseven.untis.core.data.cache.DiskCache
import com.sapuseven.untis.core.data.mapper.toData
import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.domain.cache.FromCache
import com.sapuseven.untis.core.domain.repository.InfoCenterRepository
import com.sapuseven.untis.core.domain.repository.MasterDataRepository
import com.sapuseven.untis.core.model.absences.Absence
import com.sapuseven.untis.core.model.messages.MessageOfDay
import com.sapuseven.untis.core.model.officehours.OfficeHour
import com.sapuseven.untis.core.model.timetable.ElementKey
import com.sapuseven.untis.core.model.timetable.Exam
import com.sapuseven.untis.core.model.timetable.Homework
import com.sapuseven.untis.core.model.user.User
import crocodile8.universal_cache.CachedSource
import crocodile8.universal_cache.time.TimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.serialization.serializer
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class UntisInfoCenterRepository @Inject constructor(
	private val masterDataRepository: MasterDataRepository,
	private val messagesApi: MessagesApi,
	private val classRegApi: ClassRegApi,
	private val absenceApi: AbsenceApi,
	private val officeHoursApi: OfficeHoursApi,
	@Named("cacheDir") private val cacheDir: File,
	private val timeProvider: TimeProvider,
) : InfoCenterRepository {
	companion object {
		private const val ONE_HOUR: Long = 60 * 60 * 1000
	}

	override fun getMessagesOfDay(
		user: User,
		day: LocalDate
	): Flow<List<MessageOfDay>> {
		return CachedSource<LocalDate, MessagesOfDayResult>(
			source = { params ->
				messagesApi.getMessagesOfDay(
					date = day,
					apiUrl = user.school.apiUrl,
					user = user.credentials?.user,
					key = user.credentials?.key
				)
			},
			cache = DiskCache(File(cacheDir, "infocenter/messagesofday"), serializer()),
			timeProvider = timeProvider
		)
			.get(day, FromCache.CACHED_THEN_LOAD.toData(), maxAge = ONE_HOUR, additionalKey = user.id)
			.map { result ->
				val allElements = masterDataRepository.getAllElements().associateBy { ElementKey(it.id, it.type) }
				result.messages.map { it.toDomain() }
			}
	}

	override fun getExams(
		user: User,
		params: InfoCenterRepository.EventsParams
	): Flow<List<Exam>> {
		return CachedSource<InfoCenterRepository.EventsParams, ExamsResult>(
			source = { params ->
				classRegApi.getExams(
					id = params.elementId,
					type = params.elementType.toData(),
					startDate = params.startDate,
					endDate = params.endDate,
					apiUrl = user.school.apiUrl,
					user = user.credentials?.user,
					key = user.credentials?.key
				)
			},
			cache = DiskCache(File(cacheDir, "infocenter/exams"), serializer()),
			timeProvider = timeProvider
		)
			.get(params, FromCache.CACHED_THEN_LOAD.toData(), maxAge = ONE_HOUR, additionalKey = user.id)
			.map { result ->
				val allElements = masterDataRepository.getAllElements().associateBy { ElementKey(it.id, it.type) }
				result.exams.map { it.toDomain(allElements) }
			}
	}

	override fun getHomework(
		user: User,
		params: InfoCenterRepository.EventsParams
	): Flow<List<Homework>> {
		return CachedSource<InfoCenterRepository.EventsParams, HomeworkResult>(
			source = { params ->
				classRegApi.getHomework(
					id = params.elementId,
					type = params.elementType.toData(),
					startDate = params.startDate,
					endDate = params.endDate,
					apiUrl = user.school.apiUrl,
					user = user.credentials?.user,
					key = user.credentials?.key
				)
			},
			cache = DiskCache(File(cacheDir, "infocenter/homework"), serializer()),
			timeProvider = timeProvider
		)
			.get(params, FromCache.CACHED_THEN_LOAD.toData(), maxAge = ONE_HOUR, additionalKey = user.id)
			.map { result ->
				val allElements = masterDataRepository.getAllElements().associateBy { ElementKey(it.id, it.type) }
				result.homeWorks.map { it.toDomain(allElements) }
			}
	}

	override fun getOfficeHours(
		user: User,
		params: InfoCenterRepository.OfficeHoursParams
	): Flow<List<OfficeHour>> {
		return CachedSource<InfoCenterRepository.OfficeHoursParams, OfficeHoursResult>(
			source = { params ->
				officeHoursApi.getOfficeHours(
					klasseId = params.classId,
					startDate = params.startDate,
					apiUrl = user.school.apiUrl,
					user = user.credentials?.user,
					key = user.credentials?.key
				)
			},
			cache = DiskCache(File(cacheDir, "infocenter/officehours"), serializer()),
			timeProvider = timeProvider
		)
			.get(params, FromCache.CACHED_THEN_LOAD.toData(), maxAge = ONE_HOUR, additionalKey = user.id)
			.map { result ->
				val allElements = masterDataRepository.getAllElements().associateBy { ElementKey(it.id, it.type) }
				result.officeHours.map { it.toDomain(allElements) }
			}

	}

	override fun getAbsences(
		user: User,
		params: InfoCenterRepository.AbsencesParams
	): Flow<List<Absence>> {
		return CachedSource<InfoCenterRepository.AbsencesParams, StudentAbsencesResult>(
			source = { params ->
				absenceApi.getStudentAbsences(
					startDate = params.startDate,
					endDate = params.endDate,
					includeExcused = params.includeExcused,
					includeUnExcused = params.includeUnExcused,
					apiUrl = user.school.apiUrl,
					user = user.credentials?.user,
					key = user.credentials?.key
				)
			},
			cache = DiskCache(File(cacheDir, "infocenter/absences"), serializer()),
			timeProvider = timeProvider
		)
			.get(params, FromCache.CACHED_THEN_LOAD.toData(), maxAge = ONE_HOUR, additionalKey = user.id)
			.map { result ->
				val allElements = masterDataRepository.getAllElements().associateBy { ElementKey(it.id, it.type) }
				val students = user.element?.let { mapOf(it.id to it) } ?: emptyMap() // TODO
				result.absences.map { it.toDomain(allElements, students) }
			}
	}
}
