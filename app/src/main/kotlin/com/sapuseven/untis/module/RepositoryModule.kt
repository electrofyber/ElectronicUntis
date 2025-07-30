package com.sapuseven.untis.module

import com.sapuseven.untis.core.data.repository.MasterDataRepository
import com.sapuseven.untis.core.data.repository.UntisMasterDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
	@Binds
	fun bindMasterDataRepository(
		implementation: UntisMasterDataRepository
	): MasterDataRepository

	/*@Binds
	fun bindInfoCenterRepository(
		implementation: UntisInfoCenterRepository
	): InfoCenterRepository

	@Binds
	fun bindMessagesRepository(
		implementation: UntisMessagesRepository
	): MessagesRepository*/
}
