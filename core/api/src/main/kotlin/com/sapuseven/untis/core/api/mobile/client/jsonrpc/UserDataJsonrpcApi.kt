package com.sapuseven.untis.core.api.mobile.client.jsonrpc

import com.sapuseven.untis.core.api.mobile.exception.UntisApiException
import com.sapuseven.untis.core.api.mobile.model.request.AppSharedSecretParams
import com.sapuseven.untis.core.api.mobile.model.request.AuthTokenParams
import com.sapuseven.untis.core.api.mobile.model.request.RequestData
import com.sapuseven.untis.core.api.mobile.model.request.UserDataParams
import com.sapuseven.untis.core.api.mobile.model.response.AppSharedSecretResponse
import com.sapuseven.untis.core.api.mobile.model.response.AuthTokenResponse
import com.sapuseven.untis.core.api.mobile.model.response.UserDataResponse
import com.sapuseven.untis.core.api.mobile.model.response.UserDataResult
import com.sapuseven.untis.core.api.mobile.model.untis.Auth
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory

open class UserDataJsonrpcApi(
	engineFactory: HttpClientEngineFactory<*>,
	config: ((HttpClientConfig<*>) -> Unit)
) : JsonrpcApiClient(
	httpClientEngineFactory = engineFactory,
	httpClientConfig = config,
) {
	open suspend fun getAppSharedSecret(
		apiUrl: String,
		user: String,
		password: String,
		token: String? = null
	): String {
		val body = RequestData(
			method = METHOD_GET_APP_SHARED_SECRET,
			params = listOf(AppSharedSecretParams(user, password, token))
		)

		val response: AppSharedSecretResponse = request(apiUrl, body).body()

		return response.result ?: throw UntisApiException(response.error)
	}

	open suspend fun getAuthToken(
		apiUrl: String,
		user: String?,
		key: String?
	): String {
		val body = RequestData(
			method = METHOD_GET_AUTH_TOKEN,
			params = listOf(AuthTokenParams(auth = Auth(user, key)))
		)

		val response: AuthTokenResponse = request(apiUrl, body).body()

		return response.result?.token ?: throw UntisApiException(response.error)
	}

	open suspend fun getUserData(
		apiUrl: String,
		user: String?,
		key: String?
	): UserDataResult {
		val body = RequestData(
			method = METHOD_GET_USER_DATA,
			params = listOf(UserDataParams(auth = Auth(user, key)))
		)

		val response: UserDataResponse = request(apiUrl, body).body()

		return response.result ?: throw UntisApiException(response.error)
	}
}
