package com.sapuseven.untis.core.data.service.config

import com.sapuseven.untis.core.api.mobile.client.base.BaseHttpClientConfig
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import javax.inject.Inject


class AuthHttpClientConfig @Inject constructor(
	private val baseConfig: BaseHttpClientConfig,
) {
	fun apply(
		config: HttpClientConfig<*>,
		tokenProvider: suspend () -> String
	) = with(config) {
		baseConfig.apply(this)

		install(Auth) {
			bearer {
				loadTokens {
					BearerTokens(
						tokenProvider(),
						null
					)
				}
			}
		}
	}
}
