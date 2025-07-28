package com.sapuseven.untis.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.sapuseven.untis.core.datastore.OldPreferenceDataStoreMigration
import com.sapuseven.untis.core.datastore.UserSettingsSerializer
import com.sapuseven.untis.core.datastore.model.Settings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val DATA_STORE_FILE_NAME = "settings.pb"

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {
	@Provides
	@Singleton
	fun provideProtoDataStore(@ApplicationContext appContext: Context): DataStore<Settings> {
		return DataStoreFactory.create(
			serializer = UserSettingsSerializer,
			produceFile = { appContext.dataStoreFile(DATA_STORE_FILE_NAME) },
			corruptionHandler = ReplaceFileCorruptionHandler {
				Settings.getDefaultInstance()
			},
			migrations = listOf(
				OldPreferenceDataStoreMigration(appContext)
			),
			scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
		)
	}
}
