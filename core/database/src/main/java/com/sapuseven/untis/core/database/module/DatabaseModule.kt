package com.sapuseven.untis.core.database.module

import android.content.Context
import androidx.room.Room
import com.sapuseven.untis.core.database.MIGRATIONS_USER_LEGACY
import com.sapuseven.untis.core.database.MIGRATION_USER_13_14
import com.sapuseven.untis.core.database.MIGRATION_USER_7_8
import com.sapuseven.untis.core.database.RoomFinderDatabase
import com.sapuseven.untis.core.database.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserDatabaseModule {
	@Provides
	@Singleton
	fun provideUserDatabase(
		@ApplicationContext context: Context
	) = Room.databaseBuilder(
		context,
		UserDatabase::class.java, "userdata.db"
	)
		.addMigrations(
			*MIGRATIONS_USER_LEGACY.toTypedArray(),
			MIGRATION_USER_7_8,
			MIGRATION_USER_13_14,
		)
		.build()

	@Provides
	@Singleton
	fun provideUserDao(db: UserDatabase) = db.userDao()
}

@Module
@InstallIn(SingletonComponent::class)
object RoomFinderDatabaseModule {
	@Provides
	@Singleton
	fun provideRoomFinderDatabase(
		@ApplicationContext context: Context
	) = Room.databaseBuilder(
		context,
		RoomFinderDatabase::class.java, "roomfinder.db"
	)
		// In old versions, each profile has their own roomfinder database file.
		// This makes it hard to run an automatic migration.
		// Since the data can be recovered easily, migration is skipped and a new database is used.
		.build()

	@Provides
	@Singleton
	fun provideRoomFinderDao(db: RoomFinderDatabase) = db.roomFinderDao()
}
