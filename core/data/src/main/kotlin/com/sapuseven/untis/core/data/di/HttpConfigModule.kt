package com.sapuseven.untis.core.data.di

import com.sapuseven.untis.core.api.mobile.client.base.BaseHttpClientConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HttpConfigModule {
	@Provides
	@Singleton
	fun provideHttpClientEngineFactory(): HttpClientEngineFactory<*> =
		CIO

	@Provides
	@Singleton
	fun provideBaseHttpClientConfig(json: Json): BaseHttpClientConfig =
		BaseHttpClientConfig(json)

	@Provides
	@Singleton
	fun provideJson(): Json = Json {
		ignoreUnknownKeys = true
		isLenient = true
		encodeDefaults = true
		prettyPrint = true // TODO only for DEV
	}
}
