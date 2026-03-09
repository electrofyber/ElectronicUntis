package com.sapuseven.untis.core.data.service.factory

import com.sapuseven.untis.core.api.rest.client.MessagesApi
import com.sapuseven.untis.core.data.service.config.AuthHttpClientConfig
import com.sapuseven.untis.core.domain.auth.TokenProvider
import com.sapuseven.untis.core.model.user.User
import io.ktor.client.engine.HttpClientEngineFactory
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class MessagesApiFactory @Inject constructor(
	private val engineFactory: HttpClientEngineFactory<*>,
	private val authConfig: AuthHttpClientConfig,
	private val tokenProvider: TokenProvider
) {
	private val cache = ConcurrentHashMap<Long, MessagesApi>()

	fun create(user: User): MessagesApi {
		return cache.getOrPut(user.id) {
			MessagesApi(user.school.api.rest, engineFactory.create()) {
				authConfig.apply(it) {
					tokenProvider.getValidToken(user)
				}
			}
		}
	}
}
