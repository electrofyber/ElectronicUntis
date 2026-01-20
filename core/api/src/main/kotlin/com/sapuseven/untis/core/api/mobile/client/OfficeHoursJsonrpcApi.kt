package com.sapuseven.untis.core.api.mobile.client

import com.sapuseven.untis.core.api.mobile.exception.UntisApiException
import com.sapuseven.untis.core.api.mobile.model.request.OfficeHoursParams
import com.sapuseven.untis.core.api.mobile.model.request.RequestData
import com.sapuseven.untis.core.api.mobile.model.response.OfficeHoursResponse
import com.sapuseven.untis.core.api.mobile.model.response.OfficeHoursResult
import com.sapuseven.untis.core.api.mobile.model.untis.Auth
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json

open class OfficeHoursJsonrpcApi(
	engineFactory: HttpClientEngineFactory<*>,
	config: ((HttpClientConfig<*>) -> Unit)? = null,
	jsonBlock: Json = DEFAULT_JSON
) : JsonrpcApiClient(
	httpClientEngineFactory = engineFactory,
	httpClientConfig = config,
	jsonBlock = jsonBlock
) {
	open suspend fun getOfficeHours(
		apiUrl: String,
		klasseId: Long,
		startDate: LocalDate,
		user: String?,
		key: String?
	): OfficeHoursResult {
		val body = RequestData(
			method = METHOD_GET_OFFICEHOURS,
			params = listOf(
				OfficeHoursParams(
					klasseId = klasseId,
					startDate = startDate,
					auth = Auth(user, key)
				)
			)
		)

		val response: OfficeHoursResponse = request(apiUrl, body).body()

		return response.result ?: throw UntisApiException(response.error)
	}
}
