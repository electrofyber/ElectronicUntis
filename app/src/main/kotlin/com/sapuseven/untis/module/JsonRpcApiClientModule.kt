package com.sapuseven.untis.module

import com.sapuseven.untis.core.api.mobile.client.AbsenceJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.ClassRegJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.MessagesJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.OfficeHoursJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.SchoolSearchJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.TimetableJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.UserDataJsonrpcApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object JsonRpcApiClientModule {
	@Provides
	@Singleton
	fun provideHttpClientEngineFactory(): HttpClientEngineFactory<*> = CIO

	@Provides
	@Singleton
	@Named("json")
	fun provideJsonHttpClient(httpClientEngineFactory: HttpClientEngineFactory<*>): HttpClient =
		HttpClient(httpClientEngineFactory.create()) {
			install(ContentNegotiation) {
				json()
			}
			install(HttpRequestRetry) {
				retryOnServerErrors(maxRetries = 3)
				retryOnException(maxRetries = 3)
				exponentialDelay()
			}
			expectSuccess = true
		}

	@Provides
	@Singleton
	fun provideSchoolSearchApi(engineFactory: HttpClientEngineFactory<*>): SchoolSearchJsonrpcApi =
		SchoolSearchJsonrpcApi(engineFactory)

	@Provides
	@Singleton
	fun provideUserDataApi(engineFactory: HttpClientEngineFactory<*>): UserDataJsonrpcApi = UserDataJsonrpcApi(engineFactory)

	@Provides
	@Singleton
	fun provideTimetableApi(engineFactory: HttpClientEngineFactory<*>): TimetableJsonrpcApi = TimetableJsonrpcApi(engineFactory)

	@Provides
	@Singleton
	fun provideMessagesApi(engineFactory: HttpClientEngineFactory<*>): MessagesJsonrpcApi = MessagesJsonrpcApi(engineFactory)

	@Provides
	@Singleton
	fun provideClassRegApi(engineFactory: HttpClientEngineFactory<*>): ClassRegJsonrpcApi = ClassRegJsonrpcApi(engineFactory)

	@Provides
	@Singleton
	fun provideAbsenceApi(engineFactory: HttpClientEngineFactory<*>): AbsenceJsonrpcApi = AbsenceJsonrpcApi(engineFactory)

	@Provides
	@Singleton
	fun provideOfficeHoursApi(engineFactory: HttpClientEngineFactory<*>): OfficeHoursJsonrpcApi = OfficeHoursJsonrpcApi(engineFactory)
}
