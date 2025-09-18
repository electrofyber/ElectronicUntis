package com.sapuseven.untis.core.domain.login

import com.sapuseven.untis.core.domain.exception.LoginException
import com.sapuseven.untis.core.domain.repository.LoginRepository
import com.sapuseven.untis.core.domain.repository.SchoolRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.timetable.School
import com.sapuseven.untis.core.model.user.UserCredentials
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
	 * If a second factor is required, a [com.sapuseven.untis.core.domain.exception.LoginException] with type [com.sapuseven.untis.core.domain.exception.LoginException.Type.REQUIRE_2_FACTOR] is thrown.
	 *
	 * @see com.sapuseven.untis.core.domain.exception.LoginException.Type.REQUIRE_2_FACTOR
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
			if (it is LoginException && it.type == LoginException.Type.REQUIRE_2_FACTOR) {
				throw it
			} else {
				return password ?: ""
			}
		}
	}
}
