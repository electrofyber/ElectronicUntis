package com.sapuseven.untis.core.datastore

import androidx.datastore.core.DataStore
import com.sapuseven.compose.protostore.data.MultiUserSettingsDataSource
import com.sapuseven.untis.core.datastore.model.GlobalSettings
import com.sapuseven.untis.core.datastore.model.Settings
import javax.inject.Inject

class GlobalSettingsDataSource @Inject constructor(
	dataStore: DataStore<Settings>
) : MultiUserSettingsDataSource<Settings, Settings.Builder, GlobalSettings, GlobalSettings.Builder>(
	dataStore
) {
	override fun getUserSettings(dataStore: Settings): GlobalSettings =
		if (dataStore.globalSettings.initialized)
			dataStore.globalSettings
		else
			GlobalSettings.getDefaultInstance()

	override suspend fun updateUserSettings(currentData: Settings, userSettings: GlobalSettings): Settings =
		currentData.toBuilder().setGlobalSettings(userSettings.toBuilder().setInitialized(true).build()).build()
}
