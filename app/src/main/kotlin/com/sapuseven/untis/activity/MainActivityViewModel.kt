package com.sapuseven.untis.activity

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapuseven.untis.core.datastore.UserSettingsDataSource
import com.sapuseven.untis.core.datastore.model.DarkTheme
import com.sapuseven.untis.core.datastore.model.UserSettings
import com.sapuseven.untis.core.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
	userRepository: UserRepository,
	userSettingsDataSource: UserSettingsDataSource,
) : ViewModel() {
	val uiState = userRepository.observeActiveUser()
		.combine(userSettingsDataSource.getSettings()) { activeUserId, userSettings ->
			if (activeUserId != null) {
				MainActivityUiState.Success(userSettings)
			} else {
				// TODO: Check if there are other users available in userRepository
				MainActivityUiState.Login
			}
		}
		.stateIn(
			scope = viewModelScope,
			initialValue = MainActivityUiState.Loading,
			started = SharingStarted.WhileSubscribed(5_000),
		)

	/*// A small “one‐time event” for deep‐link data
	private val _pendingIntentData = MutableStateFlow<String?>(null)
	val pendingIntentData: StateFlow<String?> = _pendingIntentData

	fun onDeepLink(dataString: String) {
		_pendingIntentData.value = dataString
	}

	fun consumeIntentData() {
		_pendingIntentData.value = null
	}*/
}

sealed interface MainActivityUiState {
	data object Loading : MainActivityUiState

	data object Login : MainActivityUiState

	data class Success(val userSettings: UserSettings) : MainActivityUiState {
		override val customThemeColor = Color(userSettings.themeColor).takeIf { userSettings.hasThemeColor() }

		override val shouldUseDarkThemeOled = userSettings.darkThemeOled

		override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
			when (userSettings.darkTheme) {
				DarkTheme.AUTO, DarkTheme.UNRECOGNIZED -> isSystemDarkTheme
				DarkTheme.LIGHT -> false
				DarkTheme.DARK -> true
			}
	}

	/**
	 * Returns `true` if the state wasn't loaded yet and it should keep showing the splash screen.
	 */
	fun shouldKeepSplashScreen() = this is Loading

	/**
	 * Returns the theme base color to be used.
	 * `null` if dynamic color should be used.
	 */
	val customThemeColor: Color? get() = null

	/**
	 * Returns `true` if dark theme with total black background should be used.
	 */
	val shouldUseDarkThemeOled: Boolean get() = false

	/**
	 * Returns `true` if dark theme should be used.
	 */
	fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) = isSystemDarkTheme
}
