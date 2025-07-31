package com.sapuseven.untis.core.domain

import com.sapuseven.untis.core.api.exception.UntisApiException
import com.sapuseven.untis.core.api.model.response.UntisErrorCode
import com.sapuseven.untis.core.data.repository.AuthRepository
import com.sapuseven.untis.core.data.repository.SchoolRepository
import com.sapuseven.untis.core.data.repository.UserRepository
import com.sapuseven.untis.core.model.School
import com.sapuseven.untis.core.model.User
import javax.inject.Inject

class LoginAndSaveUserUseCase @Inject constructor(
	private val authRepository: AuthRepository,
	private val userRepository: UserRepository,
	private val schoolRepository: SchoolRepository,
) {
	suspend operator fun invoke(
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

		val appSharedSecret = loadAppSharedSecret(school, username, password, secondFactor)
		val userData = authRepository.getUserData(school, username, appSharedSecret).getOrThrow()

		val userId = userRepository.updateUser(
			User(
				id = 0L,
				displayName = displayName ?: userData.userData.displayName,
				school = school,
				user = username,
				key = appSharedSecret,
				anonymous = username == null && appSharedSecret == null,
			), userData.masterData
		)

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
	): String? {
		return authRepository.getAppSharedSecret(
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
