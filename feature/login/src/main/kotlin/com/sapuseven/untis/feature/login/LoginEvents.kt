package com.sapuseven.untis.feature.login

sealed class LoginEvents {
	data object ClearFocus : LoginEvents()
}
