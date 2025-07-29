package com.sapuseven.untis.core.data.di

import com.sapuseven.untis.core.data.repository.AuthRepository
import com.sapuseven.untis.core.data.repository.MasterDataRepository
import com.sapuseven.untis.core.data.repository.SchoolRepository
import com.sapuseven.untis.core.data.repository.TimetableRepository
import com.sapuseven.untis.core.data.repository.UntisAuthRepository
import com.sapuseven.untis.core.data.repository.UntisMasterDataRepository
import com.sapuseven.untis.core.data.repository.UntisSchoolRepository
import com.sapuseven.untis.core.data.repository.UntisTimetableRepository
import com.sapuseven.untis.core.data.repository.UserRepository
import com.sapuseven.untis.core.data.repository.UserRepositoryImpl
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
		impl: UntisAuthRepository,
	): AuthRepository

	@Binds
	fun bindMasterDataRepository(
		impl: UntisMasterDataRepository,
	): MasterDataRepository
}
