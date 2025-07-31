package com.sapuseven.untis.core.domain

import com.sapuseven.untis.core.api.exception.UntisApiException
import com.sapuseven.untis.core.api.model.response.UntisErrorCode
import com.sapuseven.untis.core.data.repository.LoginRepository
import com.sapuseven.untis.core.data.repository.SchoolRepository
import com.sapuseven.untis.core.data.repository.UserRepository
import com.sapuseven.untis.core.model.School
import com.sapuseven.untis.core.model.UserCredentials
import javax.inject.Inject

class LoginAndSaveUserUseCase @Inject constructor(
	private val loginRepository: LoginRepository,
	private val userRepository: UserRepository,
	private val schoolRepository: SchoolRepository,
) {
	suspend operator fun invoke(
		existingUserId: Long? = null,
		schoolName: String,
		displayName: String? = null,
		username: String? = null,
		password: String? = null,
		secondFactor: String? = null,
		apiUrl: String? = null,
	): Result<Long> = runCatching {
		val school = loadSchoolInfo(schoolName, apiUrl) ?: run {
			error("Invalid school")
			//errorText = com.sapuseven.untis.feature.login.R.string.logindatainput_error_invalid_school
			//return@launch
		}

		val credentials = username?.let {
			UserCredentials(
				username,
				loadAppSharedSecret(school, username, password, secondFactor)
			)
		}
		val userId = loginRepository.persistUser(existingUserId, displayName, school, credentials).getOrThrow()
		userRepository.switchUser(userId)
		userId
	}

	private suspend fun loadSchoolInfo(
		schoolName: String,
		apiUrl: String? = null,
	): School? {
		return apiUrl?.let {
			School(
				name = schoolName,
				displayName = schoolName,
				apiUrl = apiUrl
			)
		} ?: run {
			schoolRepository.searchSchool(schoolName).getOrThrow()
		}
	}

	/**
	 * This method tries to get the app secret from the supplied password.
	 *
	 * If the call fails, the password is assumed to be the app secret already and is returned directly.
	 * If a second factor is required, the corresponding [UntisApiException] is thrown.
	 *
	 * @see UntisErrorCode.REQUIRE2_FACTOR_AUTHENTICATION_TOKEN
	 */
	private suspend fun loadAppSharedSecret(
		school: School,
		username: String? = null,
		password: String? = null,
		secondFactor: String? = null
	): String {
		return loginRepository.getAppSharedSecret(
			school.apiUrl,
			username,
			password,
			secondFactor
		).getOrElse {
			if (it is UntisApiException) {
				// If it is an Untis error, there are 2 possible cases:
				//  1. 2FA required: Throw it and show the second factor input field
				//  2. Bad credentials: Assume the supplied password is already an app secret and pass it through
				if (it.error?.code == UntisErrorCode.REQUIRE2_FACTOR_AUTHENTICATION_TOKEN) throw it
				else password ?: ""
			} else throw it // Throw all other errors
		}
	}
}
