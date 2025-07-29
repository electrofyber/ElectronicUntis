package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.api.client.UserDataApi
import com.sapuseven.untis.core.api.model.response.UserDataResult
import com.sapuseven.untis.core.model.School
import javax.inject.Inject

interface AuthRepository {
	suspend fun getAppSharedSecret(
		apiUrl: String,
		username: String? = null,
		password: String? = null,
		secondFactor: String? = null
	): String?

	suspend fun getUserData(
		school: School,
		username: String?,
		appSharedSecret: String?,
	): Result<UserDataResult>
}

class UntisAuthRepository @Inject constructor(
	private val userDataApi: UserDataApi,
) : AuthRepository {
	override suspend fun getAppSharedSecret(
		apiUrl: String,
		username: String?,
		password: String?,
		secondFactor: String?
	): String? = runCatching {
		userDataApi.getAppSharedSecret(
			apiUrl,
			username ?: "",
			password ?: "",
			secondFactor
		)
	}.getOrNull()

	override suspend fun getUserData(
		school: School,
		username: String?,
		appSharedSecret: String?,
	): Result<UserDataResult> = runCatching {
		userDataApi.getUserData(school.apiUrl, username, appSharedSecret)
	}

	/*private fun buildUser(
		apiHost: String,
		appSharedSecret: String,
		schoolInfo: SchoolInfo,
		userData: UserDataResult
	): UserEntity {
		val userEntity = UserEntity(
			existingUserId ?: 0,
			loginData.profileName.value.orEmpty(),
			apiHost,
			schoolInfo,
			null,
			if (loginData.anonymous.value != true) loginData.username.value else null,
			if (loginData.anonymous.value != true) appSharedSecret else null,
			loginData.anonymous.value == true,
			userData.masterData.timeGrid ?: TimeGrid.generateDefault(),
			userData.masterData.timeStamp,
			userData.userData,
			userData.settings,
		)
		return user
	}*/
}
