package com.sapuseven.untis.core.data.di

import com.sapuseven.untis.core.api.mobile.client.base.BaseHttpClientConfig
import com.sapuseven.untis.core.api.mobile.client.jsonrpc.AbsenceJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.jsonrpc.ClassRegJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.jsonrpc.MessagesJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.jsonrpc.OfficeHoursJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.jsonrpc.SchoolSearchJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.jsonrpc.TimetableJsonrpcApi
import com.sapuseven.untis.core.api.mobile.client.jsonrpc.UserDataJsonrpcApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.engine.HttpClientEngineFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object JsonRpcApiModule {
	@Provides
	@Singleton
	fun provideSchoolSearchApi(
		engineFactory: HttpClientEngineFactory<*>,
		clientConfig: BaseHttpClientConfig
	): SchoolSearchJsonrpcApi =
		SchoolSearchJsonrpcApi(engineFactory, clientConfig::apply)

	@Provides
	@Singleton
	fun provideUserDataApi(
		engineFactory: HttpClientEngineFactory<*>,
		clientConfig: BaseHttpClientConfig
	): UserDataJsonrpcApi =
		UserDataJsonrpcApi(engineFactory, clientConfig::apply)

	@Provides
	@Singleton
	fun provideTimetableApi(
		engineFactory: HttpClientEngineFactory<*>,
		clientConfig: BaseHttpClientConfig
	): TimetableJsonrpcApi =
		TimetableJsonrpcApi(engineFactory, clientConfig::apply)

	@Provides
	@Singleton
	fun provideMessagesApi(
		engineFactory: HttpClientEngineFactory<*>,
		clientConfig: BaseHttpClientConfig
	): MessagesJsonrpcApi =
		MessagesJsonrpcApi(engineFactory, clientConfig::apply)

	@Provides
	@Singleton
	fun provideClassRegApi(
		engineFactory: HttpClientEngineFactory<*>,
		clientConfig: BaseHttpClientConfig
	): ClassRegJsonrpcApi =
		ClassRegJsonrpcApi(engineFactory, clientConfig::apply)

	@Provides
	@Singleton
	fun provideAbsenceApi(
		engineFactory: HttpClientEngineFactory<*>,
		clientConfig: BaseHttpClientConfig
	): AbsenceJsonrpcApi =
		AbsenceJsonrpcApi(engineFactory, clientConfig::apply)

	@Provides
	@Singleton
	fun provideOfficeHoursApi(
		engineFactory: HttpClientEngineFactory<*>,
		clientConfig: BaseHttpClientConfig
	): OfficeHoursJsonrpcApi =
		OfficeHoursJsonrpcApi(engineFactory, clientConfig::apply)
}
