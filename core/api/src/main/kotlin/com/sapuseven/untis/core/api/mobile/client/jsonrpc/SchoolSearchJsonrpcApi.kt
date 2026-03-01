package com.sapuseven.untis.core.api.mobile.client.jsonrpc

import com.sapuseven.untis.core.api.mobile.exception.UntisApiException
import com.sapuseven.untis.core.api.mobile.model.request.RequestData
import com.sapuseven.untis.core.api.mobile.model.request.SchoolSearchParams
import com.sapuseven.untis.core.api.mobile.model.response.SchoolSearchResponse
import com.sapuseven.untis.core.api.mobile.model.response.SchoolSearchResult
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import kotlinx.serialization.ExperimentalSerializationApi

open class SchoolSearchJsonrpcApi(
	engineFactory: HttpClientEngineFactory<*>,
	config: ((HttpClientConfig<*>) -> Unit)
) : JsonrpcApiClient(
	httpClientEngineFactory = engineFactory,
	httpClientConfig = config,
) {
	@OptIn(ExperimentalSerializationApi::class)
	open suspend fun searchSchools(
		search: String? = null,
		schoolId: Long = 0,
		schoolName: String = ""
	): SchoolSearchResult {
		val body = RequestData(
			method = METHOD_SEARCH_SCHOOLS,
			params = listOf(SchoolSearchParams(search, schoolId, schoolName))
		)

		val response: SchoolSearchResponse = request(DEFAULT_SCHOOLSEARCH_URL, body).body()

		return response.result ?: throw UntisApiException(response.error)
	}
}
