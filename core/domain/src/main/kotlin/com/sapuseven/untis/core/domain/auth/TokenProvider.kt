package com.sapuseven.untis.core.domain.auth

interface TokenProvider {
	suspend fun getValidToken(): String
}
