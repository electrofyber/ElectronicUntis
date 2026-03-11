package com.sapuseven.untis.feature.notifications

import com.sapuseven.untis.core.domain.worker.TimetableHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface NotificationModule {
	@Binds
	@IntoSet
	fun bindNotificationScheduler(impl: NotificationScheduler): TimetableHandler
}
