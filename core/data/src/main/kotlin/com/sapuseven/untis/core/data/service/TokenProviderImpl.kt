package com.sapuseven.untis.core.data.service

import com.sapuseven.untis.core.api.mobile.client.jsonrpc.UserDataJsonrpcApi
import com.sapuseven.untis.core.domain.auth.TokenProvider
import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenProviderImpl @Inject constructor(
	private val api: UserDataJsonrpcApi,
	private val clock: Clock,
	private val zone: TimeZone,
) : TokenProvider {

	private val mutex = Mutex()
	private var token: String? = null

	override suspend fun getValidToken(user: User): String =
		mutex.withLock {
			// TODO: Check validity and only renew if expired
			if (token == null /*|| clock.now().toLocalDateTime(zone) >= token.expiresAt*/) {
				token = loadToken(user).getOrThrow()
			}
			token!!
		}

	private suspend fun loadToken(user: User, ) = runCatching {
		api.getAuthToken(user.school.api.jsonRpc, user.credentials?.user, user.credentials?.key)
	}
}
