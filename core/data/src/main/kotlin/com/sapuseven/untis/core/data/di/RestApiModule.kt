package com.sapuseven.untis.core.data.di

import com.sapuseven.untis.core.data.service.config.AuthHttpClientConfig
import com.sapuseven.untis.core.data.service.factory.MessagesApiFactory
import com.sapuseven.untis.core.domain.auth.TokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.engine.HttpClientEngineFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RestApiModule {
	@Provides
	@Singleton
	fun provideMessagesApiFactory(
		engineFactory: HttpClientEngineFactory<*>,
		authConfig: AuthHttpClientConfig,
		tokenProvider: TokenProvider,
	): MessagesApiFactory = MessagesApiFactory(engineFactory, authConfig, tokenProvider)
}
