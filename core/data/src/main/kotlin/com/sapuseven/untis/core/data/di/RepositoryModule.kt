package com.sapuseven.untis.core.data.di

import com.sapuseven.untis.core.data.repository.UntisLoginRepository
import com.sapuseven.untis.core.data.repository.UntisMasterDataRepository
import com.sapuseven.untis.core.data.repository.UntisSchoolRepository
import com.sapuseven.untis.core.data.repository.UntisTimetableRepository
import com.sapuseven.untis.core.data.repository.UserRepositoryImpl
import com.sapuseven.untis.core.domain.repository.LoginRepository
import com.sapuseven.untis.core.domain.repository.MasterDataRepository
import com.sapuseven.untis.core.domain.repository.SchoolRepository
import com.sapuseven.untis.core.domain.repository.TimetableRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {
	@Binds
	fun bindUserRepository(
		impl: UserRepositoryImpl,
	): UserRepository

	@Binds
	fun bindTimetableRepository(
		impl: UntisTimetableRepository,
	): TimetableRepository

	@Binds
	fun bindSchoolRepository(
		impl: UntisSchoolRepository,
	): SchoolRepository

	@Binds
	fun bindAuthRepository(
		impl: UntisLoginRepository,
	): LoginRepository

	@Binds
	fun bindMasterDataRepository(
		impl: UntisMasterDataRepository,
	): MasterDataRepository

	@Binds
	fun bindRoomFinderRepository(
		impl: com.sapuseven.untis.core.data.repository.RoomFinderRepository,
	): com.sapuseven.untis.core.domain.repository.RoomFinderRepository
}
