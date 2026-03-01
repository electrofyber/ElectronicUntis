package com.sapuseven.untis.core.data.di

import com.sapuseven.untis.core.api.mobile.client.base.BaseHttpClientConfig
import com.sapuseven.untis.core.api.rest.client.MessagesApi
import com.sapuseven.untis.core.domain.auth.TokenProvider
import com.sapuseven.untis.core.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RestApiModule {
	@Provides
	@Singleton
	fun provideRestBaseUrl(
		userRepository: UserRepository
	): String =
		userRepository.getActiveUser().school.api.rest

	class AuthHttpClientConfig @Inject constructor(
		private val baseConfig: BaseHttpClientConfig,
		private val tokenProvider: TokenProvider
	) {
		fun apply(config: HttpClientConfig<*>) = with(config) {
			baseConfig.apply(this)

			install(Auth) {
				bearer {
					loadTokens {
						BearerTokens(
							tokenProvider.getValidToken(),
							null
						)
					}
				}
			}
		}
	}

	@Provides
	@Singleton
	fun provideMessagesApi(
		baseUrl: String,
		engineFactory: HttpClientEngineFactory<*>,
		authConfig: AuthHttpClientConfig
	): MessagesApi = MessagesApi(baseUrl, engineFactory.create(), authConfig::apply)
}
