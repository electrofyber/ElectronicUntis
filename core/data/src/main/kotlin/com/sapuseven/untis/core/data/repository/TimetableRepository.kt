package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.api.client.TimetableApi
import com.sapuseven.untis.core.api.model.response.PeriodDataResult
import com.sapuseven.untis.core.api.model.response.TimetableResult
import com.sapuseven.untis.core.api.model.untis.absence.StudentAbsence
import com.sapuseven.untis.core.api.model.untis.enumeration.ElementType
import com.sapuseven.untis.core.api.model.untis.timetable.Period
import com.sapuseven.untis.core.data.cache.DiskCache
import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.data.repository.TimetableRepository.TimetableParams
import com.sapuseven.untis.core.database.entity.UserDao
import com.sapuseven.untis.core.model.Timetable
import crocodile8.universal_cache.CachedSource
import crocodile8.universal_cache.FromCache
import crocodile8.universal_cache.time.TimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus
import kotlinx.serialization.serializer
import java.io.File
import javax.inject.Inject
import javax.inject.Named

interface TimetableRepository {
	suspend fun getTimetable(params: TimetableParams, fromCache: FromCache): Flow<Timetable>

	suspend fun getPeriodData(periods: Set<Period>): Result<PeriodDataResult>

	suspend fun postLessonTopic(periodId: Long, lessonTopic: String): Result<Boolean>

	suspend fun postAbsence(
		periodId: Long,
		studentId: Long,
		startTime: LocalTime,
		endTime: LocalTime
	): Result<List<StudentAbsence>>

	suspend fun deleteAbsence(absenceId: Long): Result<Boolean>

	suspend fun postAbsencesChecked(periodIds: Set<Long>): Result<Unit>

	data class TimetableParams(
		val elementId: Long,
		val elementType: ElementType,
		val startDate: LocalDate,
		val endDate: LocalDate = startDate.plus(DatePeriod(days = 5 /*TODO*/))
	)
}

class UntisTimetableRepository @Inject constructor(
	private val userRepository: UserRepository,
	private val api: TimetableApi,
	@Named("cacheDir") private val cacheDir: File,
	private val timeProvider: TimeProvider,
	private val userDao: UserDao,
) : TimetableRepository {
	override suspend fun getTimetable(
		params: TimetableParams,
		fromCache: FromCache
	): Flow<Timetable> {
		// TODO: Add error handling
		val user = userRepository.getActiveUser()
		return CachedSource<TimetableParams, TimetableResult>(
			source = { params ->
				api.getTimetable(
					id = params.elementId,
					type = params.elementType,
					startDate = params.startDate,
					endDate = params.endDate,
					masterDataTimestamp = 0L,//TODO user.masterData.timestamp,
					apiUrl = user.school.apiUrl,
					user = user.credentials?.user,
					key = user.credentials?.key
				)
			},
			cache = DiskCache(File(cacheDir, "timetable"), serializer()),
			timeProvider = timeProvider
		).getRaw(
			params = params,
			fromCache = fromCache,
			additionalKey = user.id
		).map { result ->
			if (!result.fromCache) {
				userDao.upsertMasterData(user.id, result.value.masterData)
			}
			result.value.timetable.toDomain(emptyMap(/* TODO: Pass allElements */)).copy(
				timestamp = result.originTimeStamp?.let(Instant::fromEpochMilliseconds) ?: Clock.System.now()
			)
		}
	}

	override suspend fun getPeriodData(periods: Set<Period>): Result<PeriodDataResult> {
		return runCatching {
			val user = userRepository.getActiveUser()
			CachedSource<Set<Period>, PeriodDataResult>(
				source = { params ->
					api.getPeriodData(
						periodIds = params.map { it.id }.toSet(),
						apiUrl = user.school.apiUrl,
						user = user.credentials?.user,
						key = user.credentials?.key
					)
				},
				cache = DiskCache(File(cacheDir, "periodData"), serializer()),
				timeProvider = timeProvider
			)
				.get(periods, FromCache.IF_FAILED, additionalKey = user.id)
				.last()
		}
	}

	override suspend fun postLessonTopic(periodId: Long, lessonTopic: String): Result<Boolean> {
		return runCatching {
			val user = userRepository.getActiveUser()
			api.postLessonTopic(
				periodId = periodId,
				lessonTopic = lessonTopic,
				apiUrl = user.school.apiUrl,
				user = user.credentials?.user,
				key = user.credentials?.key
			)
		}
	}

	override suspend fun postAbsence(
		periodId: Long,
		studentId: Long,
		startTime: LocalTime,
		endTime: LocalTime
	): Result<List<StudentAbsence>> {
		return runCatching {
			val user = userRepository.getActiveUser()
			api.postAbsence(
				periodId = periodId,
				studentId = studentId,
				startTime = startTime,
				endTime = endTime,
				apiUrl = user.school.apiUrl,
				user = user.credentials?.user,
				key = user.credentials?.key
			)
		}
	}

	override suspend fun deleteAbsence(absenceId: Long): Result<Boolean> {
		return runCatching {
			val user = userRepository.getActiveUser()
			api.deleteAbsence(
				absenceId = absenceId,
				apiUrl = user.school.apiUrl,
				user = user.credentials?.user,
				key = user.credentials?.key
			)
		}
	}

	override suspend fun postAbsencesChecked(periodIds: Set<Long>): Result<Unit> {
		return runCatching {
			val user = userRepository.getActiveUser()
			api.postAbsencesChecked(
				periodIds = periodIds,
				apiUrl = user.school.apiUrl,
				user = user.credentials?.user,
				key = user.credentials?.key
			)
		}
	}
}
