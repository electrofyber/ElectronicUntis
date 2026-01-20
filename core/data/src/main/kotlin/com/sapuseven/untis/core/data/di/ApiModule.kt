package com.sapuseven.untis.core.data.di

import com.sapuseven.untis.core.api.rest.client.MessagesApi
import com.sapuseven.untis.core.data.client.UntisMessagesRestApiClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ApiModule {
	@Binds
	fun bindMessagesApi(
		impl: UntisMessagesRestApiClient,
	): MessagesApi
}
