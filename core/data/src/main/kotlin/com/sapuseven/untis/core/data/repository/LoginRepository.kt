package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.api.mobile.client.UserDataJsonrpcApi
import com.sapuseven.untis.core.api.mobile.exception.UntisApiException
import com.sapuseven.untis.core.api.mobile.model.response.UntisErrorCode
import com.sapuseven.untis.core.api.mobile.model.untis.MasterData
import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.Right
import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.database.entity.UserDao
import com.sapuseven.untis.core.domain.exception.LoginException
import com.sapuseven.untis.core.domain.repository.LoginRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.School
import com.sapuseven.untis.core.model.timetable.TimeGrid
import com.sapuseven.untis.core.model.timetable.TimeGridDay
import com.sapuseven.untis.core.model.timetable.TimeGridUnit
import com.sapuseven.untis.core.model.user.User
import com.sapuseven.untis.core.model.user.UserCredentials
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import javax.inject.Inject

class UntisLoginRepository @Inject constructor(
	private val userDao: UserDao,
	private val userDataApi: UserDataJsonrpcApi,
	private val userRepository: UserRepository,
) : LoginRepository {
	override suspend fun getAppSharedSecret(
		apiUrl: String,
		username: String?,
		password: String?,
		secondFactor: String?
	): Result<String> = runCatching {
		try {
			userDataApi.getAppSharedSecret(
				apiUrl,
				username ?: "",
				password ?: "",
				secondFactor
			)
		} catch (e: UntisApiException) {
			// If it is an Untis error, there are 2 possible cases:
			//  1. 2FA required: Throw it and show the second factor input field
			//  2. Bad credentials: Assume the supplied password is already an app secret and pass it through
			if (e.error?.code == UntisErrorCode.REQUIRE2_FACTOR_AUTHENTICATION_TOKEN)
				throw LoginException(LoginException.Type.REQUIRE_2_FACTOR, e.message)
			else password ?: ""
		} catch (e: Exception) {
			throw LoginException(LoginException.Type.UNKNOWN, e.message)
		}
	}

	override suspend fun persistUser(
		existingUserId: Long?,
		displayName: String?,
		school: School,
		credentials: UserCredentials?,
	): Result<Long> = runCatching {
		val dto = userDataApi.getUserData(school.apiUrl, credentials?.user, credentials?.key)

		val user = dto.run {
			User(
				id = existingUserId ?: 0L,
				name = userData.displayName,
				displayName = displayName ?: userData.displayName,
				school = school,
				credentials = credentials,
				element = userData.elemType?.let { elementType ->
					Element.personal(
						id = userData.elemId,
						type = elementType.toDomain(),
						name = userData.displayName,
					)
				},
				rights = userData.rights.map(Right::toDomain),
				timeGrid = masterData.timeGrid?.toDomain() ?: defaultTimeGrid(1..5, 6..22)
			)
		}

		val userId = userRepository.updateUser(user)
		insertMasterData(userId, dto.masterData)
		userId
	}

	private suspend fun insertMasterData(userId: Long, masterData: MasterData) {
		userDao.deleteMasterData(userId)
		userDao.insertMasterData(userId, masterData)
	}

	fun defaultTimeGrid(dayRange: IntRange, hourRange: IntRange): TimeGrid {
		val unitsForDay = hourRange.map { hourIndex -> // Range of hours to include
			TimeGridUnit(
				hourIndex.toString(),
				LocalTime(hourIndex, 0),
				LocalTime(if (hourIndex < 23) hourIndex + 1 else 0, 0)
			)
		}

		return TimeGrid(
			// Range of week days to include (1 = Monday, ..., 7 = Sunday)
			dayRange.map {
				TimeGridDay(DayOfWeek(it), unitsForDay)
			})
	}

	/*fun TimeGrid.hasEqualDays(): Boolean {
		for (i in 1..days.size) {
			if (days[i] != days[i - 1])
				return false
		}

		return true
	}*/
}
