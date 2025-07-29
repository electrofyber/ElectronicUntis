package com.sapuseven.untis.ui.pages.main

import androidx.lifecycle.ViewModel
import com.sapuseven.untis.core.data.repository.UserRepository
import com.sapuseven.untis.core.data.repository.UserState
import com.sapuseven.untis.core.datastore.UserSettingsDataSource
import com.sapuseven.untis.core.datastore.model.UserSettings
import com.sapuseven.untis.ui.navigation.AppNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	userRepository: UserRepository,
	userSettingsDataSource: UserSettingsDataSource,
	val appNavigator: AppNavigator
) : ViewModel() {

	// Expose the userState directly
	val userState: StateFlow<UserState> = userRepository.userState

	// Expose the Flow<UserSettings> from the repository so Compose can collect it
	val userSettingsFlow: Flow<UserSettings> = userSettingsDataSource.getSettings()

	// A small “one‐time event” for deep‐link data
	private val _pendingIntentData = MutableStateFlow<String?>(null)
	val pendingIntentData: StateFlow<String?> = _pendingIntentData

	fun onDeepLink(dataString: String) {
		_pendingIntentData.value = dataString
	}

	fun consumeIntentData() {
		_pendingIntentData.value = null
	}
}
