package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.api.client.UserDataApi
import com.sapuseven.untis.core.api.model.untis.enumeration.Right
import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.model.Element
import com.sapuseven.untis.core.model.School
import com.sapuseven.untis.core.model.TimeGrid
import com.sapuseven.untis.core.model.TimeGridDay
import com.sapuseven.untis.core.model.TimeGridUnit
import com.sapuseven.untis.core.model.User
import com.sapuseven.untis.core.model.UserCredentials
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import javax.inject.Inject

interface LoginRepository {
	suspend fun getAppSharedSecret(
		apiUrl: String,
		username: String? = null,
		password: String? = null,
		secondFactor: String? = null
	): Result<String>

	suspend fun persistUser(
		existingUserId: Long? = null,
		displayName: String? = null,
		school: School,
		credentials: UserCredentials?,
	): Result<Long>
}

class UntisLoginRepository @Inject constructor(
	private val userDataApi: UserDataApi,
	private val userRepository: UserRepository,
) : LoginRepository {
	override suspend fun getAppSharedSecret(
		apiUrl: String,
		username: String?,
		password: String?,
		secondFactor: String?
	): Result<String> = runCatching {
		userDataApi.getAppSharedSecret(
			apiUrl,
			username ?: "",
			password ?: "",
			secondFactor
		)
	}

	override suspend fun persistUser(
		existingUserId: Long?,
		displayName: String?,
		school: School,
		credentials: UserCredentials?,
	): Result<Long> = runCatching {
		userDataApi.getUserData(school.apiUrl, credentials?.user, credentials?.key).run {
			val user = User(
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

			userRepository.updateUser(user, masterData)
		}
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
