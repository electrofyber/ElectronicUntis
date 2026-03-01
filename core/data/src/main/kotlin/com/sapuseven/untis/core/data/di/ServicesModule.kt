package com.sapuseven.untis.core.data.di

import com.sapuseven.untis.core.data.service.TokenProviderImpl
import com.sapuseven.untis.core.data.service.WeekLogicServiceImpl
import com.sapuseven.untis.core.domain.auth.TokenProvider
import com.sapuseven.untis.core.domain.timetable.WeekLogicService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
internal interface ServicesModule {
	@Binds
	fun bindWeekLogicService(
		impl: WeekLogicServiceImpl,
	): WeekLogicService

	@Binds
	fun bindTokenProvider(
		impl: TokenProviderImpl,
	): TokenProvider
}
