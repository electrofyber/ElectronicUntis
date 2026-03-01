package com.sapuseven.untis.core.api.mobile.client.jsonrpc

import com.sapuseven.untis.core.api.mobile.exception.UntisApiException
import com.sapuseven.untis.core.api.mobile.model.request.ExamsParams
import com.sapuseven.untis.core.api.mobile.model.request.HomeworkParams
import com.sapuseven.untis.core.api.mobile.model.request.RequestData
import com.sapuseven.untis.core.api.mobile.model.response.ExamsResponse
import com.sapuseven.untis.core.api.mobile.model.response.ExamsResult
import com.sapuseven.untis.core.api.mobile.model.response.HomeworkResponse
import com.sapuseven.untis.core.api.mobile.model.response.HomeworkResult
import com.sapuseven.untis.core.api.mobile.model.untis.Auth
import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.ElementType
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import kotlinx.datetime.LocalDate

open class ClassRegJsonrpcApi(
	engineFactory: HttpClientEngineFactory<*>,
	config: ((HttpClientConfig<*>) -> Unit)
) : JsonrpcApiClient(
	httpClientEngineFactory = engineFactory,
	httpClientConfig = config,
) {
	open suspend fun getExams(
		apiUrl: String,
		id: Long,
		type: ElementType,
		startDate: LocalDate,
		endDate: LocalDate,
		user: String?,
		key: String?
	): ExamsResult {
		val body = RequestData(
			method = METHOD_GET_EXAMS,
			params = listOf(
				ExamsParams(
					id = id,
					type = type,
					startDate = startDate,
					endDate = endDate,
					auth = Auth(user, key)
				)
			)
		)

		val response: ExamsResponse = request(apiUrl, body).body()

		return response.result ?: throw UntisApiException(response.error)
	}

	open suspend fun getHomework(
		apiUrl: String,
		id: Long,
		type: ElementType,
		startDate: LocalDate,
		endDate: LocalDate,
		user: String?,
		key: String?
	): HomeworkResult {
		val body = RequestData(
			method = METHOD_GET_HOMEWORK,
			params = listOf(
				HomeworkParams(
					id = id,
					type = type,
					startDate = startDate,
					endDate = endDate,
					auth = Auth(user, key)
				)
			)
		)

		val response: HomeworkResponse = request(apiUrl, body).body()

		return response.result ?: throw UntisApiException(response.error)
	}
}
