package com.sapuseven.untis.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.sapuseven.compose.protostore.data.MultiUserSettingsDataSource
import com.sapuseven.untis.core.datastore.model.Settings
import com.sapuseven.untis.core.datastore.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserSettingsDataSource @Inject constructor(
	private val userProvider: UserProvider,
	dataStore: DataStore<Settings>
) : MultiUserSettingsDataSource<Settings, Settings.Builder, UserSettings, UserSettings.Builder>(
	dataStore
) {
	fun getSettings(userId: Long): Flow<UserSettings> {
		return getAllSettings().map { userSettings -> userSettings.userSettingsMap.getOrDefault(userId, UserSettings.getDefaultInstance()) }
	}

	private fun getUserSettings(dataStore: Settings, userId: Long?): UserSettings {
		Log.d("SettingsRepository", "DataStore getUserSettings #$userId")

		return dataStore.userSettingsMap.getOrDefault(userId, UserSettings.getDefaultInstance())
	}

	override fun getUserSettings(dataStore: Settings): UserSettings {
		return getUserSettings(dataStore, userProvider.optionalUserId())
	}

	override suspend fun updateUserSettings(currentData: Settings, userSettings: UserSettings): Settings {
		return currentData.toBuilder()
			.apply {
				putUserSettings(userProvider.requireUserId(), userSettings)
			}
			.build()
	}
}

fun <T> T.withDefault(isPresent: Boolean, defaultValue: T): T = if (isPresent) this else defaultValue
