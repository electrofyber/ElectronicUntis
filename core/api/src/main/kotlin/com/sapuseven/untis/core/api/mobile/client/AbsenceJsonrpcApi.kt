package com.sapuseven.untis.core.api.mobile.client

import com.sapuseven.untis.core.api.mobile.exception.UntisApiException
import com.sapuseven.untis.core.api.mobile.model.request.RequestData
import com.sapuseven.untis.core.api.mobile.model.request.StudentAbsencesParams
import com.sapuseven.untis.core.api.mobile.model.response.StudentAbsencesResponse
import com.sapuseven.untis.core.api.mobile.model.response.StudentAbsencesResult
import com.sapuseven.untis.core.api.mobile.model.untis.Auth
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json

open class AbsenceJsonrpcApi(
	engineFactory: HttpClientEngineFactory<*>,
	config: ((HttpClientConfig<*>) -> Unit)? = null,
	jsonBlock: Json = DEFAULT_JSON
) : JsonrpcApiClient(
	httpClientEngineFactory = engineFactory,
	httpClientConfig = config,
	jsonBlock = jsonBlock
) {
	open suspend fun getStudentAbsences(
		apiUrl: String,
		startDate: LocalDate,
		endDate: LocalDate,
		includeExcused: Boolean,
		includeUnExcused: Boolean,
		user: String?,
		key: String?
	): StudentAbsencesResult {
		val body = RequestData(
			method = METHOD_GET_ABSENCES,
			params = listOf(
				StudentAbsencesParams(
					startDate = startDate,
					endDate = endDate,
					includeExcused = includeExcused,
					includeUnExcused = includeUnExcused,
					auth = Auth(user, key)
				)
			)
		)

		val response: StudentAbsencesResponse = request(apiUrl, body).body()

		return response.result ?: throw UntisApiException(response.error)
	}
}
