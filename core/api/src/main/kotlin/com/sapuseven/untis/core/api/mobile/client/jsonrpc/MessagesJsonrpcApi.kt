package com.sapuseven.untis.core.api.mobile.client.jsonrpc

import com.sapuseven.untis.core.api.mobile.exception.UntisApiException
import com.sapuseven.untis.core.api.mobile.model.request.MessagesOfDayParams
import com.sapuseven.untis.core.api.mobile.model.request.RequestData
import com.sapuseven.untis.core.api.mobile.model.response.MessagesOfDayResponse
import com.sapuseven.untis.core.api.mobile.model.response.MessagesOfDayResult
import com.sapuseven.untis.core.api.mobile.model.untis.Auth
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import kotlinx.datetime.LocalDate

open class MessagesJsonrpcApi(
	engineFactory: HttpClientEngineFactory<*>,
	config: ((HttpClientConfig<*>) -> Unit)
) : JsonrpcApiClient(
	httpClientEngineFactory = engineFactory,
	httpClientConfig = config,
) {
	open suspend fun getMessagesOfDay(
		apiUrl: String,
		date: LocalDate,
		user: String?,
		key: String?
	): MessagesOfDayResult {
		val body = RequestData(
			method = METHOD_GET_MESSAGES,
			params = listOf(
				MessagesOfDayParams(
					date = date,
					auth = Auth(user, key)
				)
			)
		)

		val response: MessagesOfDayResponse = request(apiUrl, body).body()

		return response.result ?: throw UntisApiException(response.error)
	}
}
