package com.sapuseven.untis.core.datastore

import androidx.datastore.core.DataStore
import com.sapuseven.compose.protostore.data.MultiUserSettingsDataSource
import com.sapuseven.untis.core.datastore.model.Settings
import com.sapuseven.untis.core.datastore.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserSettingsDataSource @Inject constructor(
	dataStore: DataStore<Settings>
) : MultiUserSettingsDataSource<Settings, UserSettings, UserSettings.Builder>(
	dataStore
) {
	fun getSettings(userId: Long): Flow<UserSettings> {
		return getAllSettings().map { userSettings -> userSettings.userSettingsMap.getOrDefault(userId, UserSettings.getDefaultInstance()) }
	}

	override fun getUserSettings(dataStore: Settings): UserSettings {
		return dataStore.userSettingsMap.getOrDefault(dataStore.activeUser, UserSettings.getDefaultInstance())
	}

	override suspend fun updateUserSettings(currentData: Settings, userSettings: UserSettings): Settings {
		return currentData.toBuilder()
			.apply {
				putUserSettings(currentData.activeUser, userSettings)
			}
			.build()
	}
}
