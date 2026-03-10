package com.sapuseven.untis.core.data.di

import com.sapuseven.untis.core.data.repository.GenericRoomFinderRepository
import com.sapuseven.untis.core.data.repository.UntisDirectMessageRepository
import com.sapuseven.untis.core.data.repository.UntisElementRepository
import com.sapuseven.untis.core.data.repository.UntisInfoCenterRepository
import com.sapuseven.untis.core.data.repository.UntisLoginRepository
import com.sapuseven.untis.core.data.repository.UntisSchoolRepository
import com.sapuseven.untis.core.data.repository.UntisSchoolYearRepository
import com.sapuseven.untis.core.data.repository.UntisTimetableRepository
import com.sapuseven.untis.core.data.repository.UserRepositoryImpl
import com.sapuseven.untis.core.domain.repository.DirectMessageRepository
import com.sapuseven.untis.core.domain.repository.ElementRepository
import com.sapuseven.untis.core.domain.repository.InfoCenterRepository
import com.sapuseven.untis.core.domain.repository.LoginRepository
import com.sapuseven.untis.core.domain.repository.RoomFinderRepository
import com.sapuseven.untis.core.domain.repository.SchoolRepository
import com.sapuseven.untis.core.domain.repository.SchoolYearRepository
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
	fun bindElementRepository(
		impl: UntisElementRepository,
	): ElementRepository

	@Binds
	fun bindSchoolYearRepository(
		impl: UntisSchoolYearRepository,
	): SchoolYearRepository

	@Binds
	fun bindRoomFinderRepository(
		impl: GenericRoomFinderRepository,
	): RoomFinderRepository

	@Binds
	fun bindInfoCenterRepository(
		impl: UntisInfoCenterRepository,
	): InfoCenterRepository

	@Binds
	fun bindDirectMessagesRepository(
		impl: UntisDirectMessageRepository,
	): DirectMessageRepository
}
