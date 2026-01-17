package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.api.client.TimetableApi
import com.sapuseven.untis.core.api.model.response.PeriodDataResult
import com.sapuseven.untis.core.api.model.response.TimetableResult
import com.sapuseven.untis.core.data.cache.DiskCache
import com.sapuseven.untis.core.data.mapper.toData
import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.database.entity.UserDao
import com.sapuseven.untis.core.domain.cache.FromCache
import com.sapuseven.untis.core.domain.repository.MasterDataRepository
import com.sapuseven.untis.core.domain.repository.TimetableRepository
import com.sapuseven.untis.core.model.absences.Absence
import com.sapuseven.untis.core.model.timetable.ElementKey
import com.sapuseven.untis.core.model.timetable.Period
import com.sapuseven.untis.core.model.timetable.PeriodDetails
import com.sapuseven.untis.core.model.timetable.Timetable
import com.sapuseven.untis.core.model.user.User
import crocodile8.universal_cache.CachedSource
import crocodile8.universal_cache.time.TimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.serialization.serializer
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class UntisTimetableRepository @Inject constructor(
	@Named("cacheDir") private val cacheDir: File,
	private val api: TimetableApi,
	private val timeProvider: TimeProvider,
	private val userDao: UserDao,
	private val masterDataRepository: MasterDataRepository,
) : TimetableRepository {
	override fun getTimetable(
		user: User,
		params: TimetableRepository.TimetableParams,
		fromCache: FromCache,
		maxAge: Long?,
	): Flow<Timetable> {
		return CachedSource<TimetableRepository.TimetableParams, TimetableResult>(
			source = { params ->
				api.getTimetable(
					id = params.elementId,
					type = params.elementType.toData(),
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
			fromCache = fromCache.toData(),
			maxAge = maxAge,
			additionalKey = user.id
		).map { result ->
			if (!result.fromCache) {
				userDao.upsertMasterData(user.id, result.value.masterData)
			}
			val allElements = masterDataRepository.getAllElements().associateBy { ElementKey(it.id, it.type) }
			result.value.timetable.toDomain(allElements).copy(
				timestamp = result.originTimeStamp?.let(Instant::fromEpochMilliseconds) ?: Clock.System.now()
			)
		}
	}

	override suspend fun getPeriodData(user: User, periods: Set<Period>): Result<Map<Long, PeriodDetails>> {
		return runCatching {
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
				.get(periods, FromCache.IF_FAILED.toData(), additionalKey = user.id)
				.map { result -> result.dataByTTId.mapValues { it.value.toDomain() } }
				.last()
		}
	}

	override suspend fun postLessonTopic(user: User, periodId: Long, lessonTopic: String): Result<Boolean> {
		return runCatching {
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
		user: User,
		periodId: Long,
		studentId: Long,
		startTime: LocalTime,
		endTime: LocalTime
	): Result<List<Absence>> {
		return runCatching {
			api.postAbsence(
				periodId = periodId,
				studentId = studentId,
				startTime = startTime,
				endTime = endTime,
				apiUrl = user.school.apiUrl,
				user = user.credentials?.user,
				key = user.credentials?.key
			).map { it.toDomain() }
		}
	}

	override suspend fun deleteAbsence(user: User,absenceId: Long): Result<Boolean> {
		return runCatching {
			api.deleteAbsence(
				absenceId = absenceId,
				apiUrl = user.school.apiUrl,
				user = user.credentials?.user,
				key = user.credentials?.key
			)
		}
	}

	override suspend fun postAbsencesChecked(user: User,periodIds: Set<Long>): Result<Unit> {
		return runCatching {
			api.postAbsencesChecked(
				periodIds = periodIds,
				apiUrl = user.school.apiUrl,
				user = user.credentials?.user,
				key = user.credentials?.key
			)
		}
	}
}
