package com.sapuseven.untis.module

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {
	/*@Binds
	fun bindGetMessagesOfDayUseCase(
		implementation: GetMessagesOfDayUseCaseImpl
	): GetMessagesOfDayUseCase

	@Binds
	fun bindGetMessagesUseCase(
		implementation: GetMessagesUseCaseImpl
	): GetMessagesUseCase

	@Binds
	fun bindGetRoomFinderItemsUseCase(
		implementation: GetRoomFinderItemsUseCaseImpl
	): GetRoomFinderItemsUseCase*/
}
