package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.api.client.UserDataApi
import com.sapuseven.untis.core.api.model.untis.enumeration.Right
import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.model.Element
import com.sapuseven.untis.core.model.School
import com.sapuseven.untis.core.model.User
import com.sapuseven.untis.core.model.UserCredentials
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
				id = existingUserId ?: -1L,
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
				rights = userData.rights.map(Right::toDomain)
			)

			userRepository.updateUser(user, masterData)
		}
	}
}
