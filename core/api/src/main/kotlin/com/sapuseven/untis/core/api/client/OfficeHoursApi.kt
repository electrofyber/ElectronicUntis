package com.sapuseven.untis.core.api.client

import com.sapuseven.untis.core.api.exception.UntisApiException
import com.sapuseven.untis.core.api.model.request.OfficeHoursParams
import com.sapuseven.untis.core.api.model.request.RequestData
import com.sapuseven.untis.core.api.model.response.OfficeHoursResponse
import com.sapuseven.untis.core.api.model.response.OfficeHoursResult
import com.sapuseven.untis.core.api.model.untis.Auth
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json

open class OfficeHoursApi(
	engineFactory: HttpClientEngineFactory<*>,
	config: ((HttpClientConfig<*>) -> Unit)? = null,
	jsonBlock: Json = DEFAULT_JSON
) : ApiClient(
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
