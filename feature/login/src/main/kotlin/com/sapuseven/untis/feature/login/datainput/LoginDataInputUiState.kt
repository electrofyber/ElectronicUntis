package com.sapuseven.untis.feature.login.datainput

import android.util.Patterns
import androidx.annotation.StringRes
import com.sapuseven.untis.core.model.user.User

data class LoginDataInputUiState(
	val isLoading: Boolean = false,
	val validate: Boolean = false,
	val formData: LoginData = LoginData(),
	@param:StringRes val errorText: Int? = null,
	val errorTextRaw: String? = null,
	val showQrError: Boolean = false,
	val showProfileUpdate: Boolean = false,
	val isExistingUser: Boolean = false,
	val isSecondFactorRequired: Boolean = false,
	val isLoggedIn: Boolean = false,
) {
	fun withLoadedUser(user: User): LoginDataInputUiState = copy(
		formData = LoginData.fromUser(user),
	)
}

data class LoginData(
	val profileName: String = "",
	val schoolName: String = "",
	val anonymous: Boolean = false,
	val username: String = "",
	val password: String = "",
	val storedPassword: String? = null,
	val secondFactor: String = "",
	val apiUrl: String = "",
) {
	val isSchoolNameValid: Boolean
		get() = schoolName.isNotEmpty()

	val isUsernameValid: Boolean
		get() = username.isNotEmpty() || anonymous

	val isApiUrlValid: Boolean
		get() = apiUrl.isEmpty() || Patterns.WEB_URL.matcher(apiUrl).matches()

	val isValid: Boolean
		get() = isSchoolNameValid && isUsernameValid && isApiUrlValid

	companion object {
		fun fromUser(user: User) = LoginData(
			profileName = user.displayName,
			schoolName = user.school.name,
			anonymous = user.isAnonymous,
			username = user.credentials?.user ?: "",
			storedPassword = user.credentials?.key,
			apiUrl = user.school.api.jsonRpc
		)
	}
}
