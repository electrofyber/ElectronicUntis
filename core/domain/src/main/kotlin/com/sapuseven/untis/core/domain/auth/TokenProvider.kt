package com.sapuseven.untis.core.domain.auth

import com.sapuseven.untis.core.model.user.User

interface TokenProvider {
	suspend fun getValidToken(user: User): String
}
