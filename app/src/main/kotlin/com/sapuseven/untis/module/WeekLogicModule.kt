package com.sapuseven.untis.module

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface WeekLogicModule {
	/*@Binds
	fun bindWeekLogicService(
		implementation: WeekLogicServiceImpl
	): WeekLogicService*/
}
