package com.sapuseven.untis.core.api.mobile.client.base

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class BaseHttpClientConfig(
	private val json: Json
) {
	fun apply(config: HttpClientConfig<*>) = with(config) {
		expectSuccess = true

		install(ContentNegotiation) {
			json(json, ContentType.Application.Json)
			json(json, ContentType("application", "json-rpc"))
		}

		install(HttpRequestRetry) {
			retryOnServerErrors(maxRetries = 3)
			retryOnException(maxRetries = 3)
			exponentialDelay()
		}
	}
}
