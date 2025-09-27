package com.sapuseven.untis.feature.settings

import android.annotation.SuppressLint
import androidx.compose.material3.ColorScheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapuseven.untis.core.datastore.GlobalSettingsDataSource
import com.sapuseven.untis.core.datastore.UserSettingsDataSource
import com.sapuseven.untis.core.domain.repository.MasterDataRepository
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@SuppressLint("NewApi")
@HiltViewModel(assistedFactory = SettingsViewModel.Factory::class)
class SettingsViewModel @AssistedInject constructor(
	internal val globalSettingsDataSource: GlobalSettingsDataSource,
	internal val userSettingsDataSource: UserSettingsDataSource,
	masterDataRepository: MasterDataRepository,
	//internal val autoMuteService: AutoMuteService,
	//@Named("json") private val httpClient: HttpClient,
	@Assisted val colorScheme: ColorScheme,
) : ViewModel() {
	@AssistedFactory
	interface Factory {
		fun create(colorScheme: ColorScheme): SettingsViewModel
	}

	init {
		/*if (autoMuteService is AutoMuteServiceZenRuleImpl) {
			autoMuteService.setUser(userRepository.currentUser!!)
		}*/
	}

	val elements: StateFlow<Map<ElementType, List<Element>>> = masterDataRepository.timetableElements.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = emptyMap()
	)

	/*private val _contributors = MutableStateFlow<List<GitHubUser>>(emptyList())
	val contributors: StateFlow<List<GitHubUser>> = _contributors

	private val _contributorsError = MutableStateFlow<Throwable?>(null)
	val contributorsError: StateFlow<Throwable?> = _contributorsError*/

	fun resetColors() = viewModelScope.launch {
		userSettingsDataSource.updateSettings {
			clearBackgroundRegular()
			clearBackgroundRegularPast()
			clearBackgroundExam()
			clearBackgroundExamPast()
			clearBackgroundIrregular()
			clearBackgroundIrregularPast()
			clearBackgroundCancelled()
			clearBackgroundCancelledPast()
		}
	}

	suspend fun loadContributors() {
		/* TODO _contributorsError.value = null

		try {
			_contributors.value = httpClient.get("$URL_GITHUB_REPOSITORY_API/contributors").body()
		} catch (e: Exception) {
			_contributorsError.value = e
		}*/
	}
}
