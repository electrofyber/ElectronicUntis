package com.sapuseven.untis.core.api.mobile.client

import com.sapuseven.untis.core.api.mobile.exception.UntisApiException
import com.sapuseven.untis.core.api.mobile.model.request.RequestData
import com.sapuseven.untis.core.api.mobile.model.request.SchoolSearchParams
import com.sapuseven.untis.core.api.mobile.model.response.SchoolSearchResponse
import com.sapuseven.untis.core.api.mobile.model.response.SchoolSearchResult
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

open class SchoolSearchJsonrpcApi(
	engineFactory: HttpClientEngineFactory<*>,
	config: ((HttpClientConfig<*>) -> Unit)? = null,
	jsonBlock: Json = JsonrpcApiClient.DEFAULT_JSON
) : JsonrpcApiClient(
	httpClientEngineFactory = engineFactory,
	httpClientConfig = config,
	jsonBlock = jsonBlock
) {
	@OptIn(ExperimentalSerializationApi::class)
	open suspend fun searchSchools(
		search: String? = null,
		schoolId: Long = 0,
		schoolName: String = ""
	): SchoolSearchResult {
		val body = RequestData(
			method = JsonrpcApiClient.METHOD_SEARCH_SCHOOLS,
			params = listOf(SchoolSearchParams(search, schoolId, schoolName))
		)

		val response: SchoolSearchResponse = request(DEFAULT_SCHOOLSEARCH_URL, body).body()

		return response.result ?: throw UntisApiException(response.error)
	}
}
