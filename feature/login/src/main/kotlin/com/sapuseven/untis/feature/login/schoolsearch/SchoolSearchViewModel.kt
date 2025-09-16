package com.sapuseven.untis.feature.login.schoolsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapuseven.untis.core.domain.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SchoolSearchViewModel @Inject constructor(
	private val schoolRepository: SchoolRepository,
) : ViewModel() {
	private val _uiState = MutableStateFlow(SchoolSearchUiState())
	val uiState: StateFlow<SchoolSearchUiState> = _uiState.asStateFlow()

	fun searchSchools(query: String) = viewModelScope.launch {
		if (query.isEmpty()) return@launch

		_uiState.update {
			it.copy(
				results = emptyList(),
				errorResId = null,
				errorRaw = null,
				isLoading = true
			)
		}

		schoolRepository.searchSchools(query).fold(
			onSuccess = { schools ->
				_uiState.update {
					it.copy(
						results = schools,
						errorResId = null,
						errorRaw = null,
						isLoading = false
					)
				}
			},
			onFailure = { error ->
				_uiState.update {
					it.copy(
						results = emptyList(),
						errorResId = null,// TODO ErrorMessageDictionary.getErrorMessageResource(e.error?.code, false)
						errorRaw = error.message.orEmpty(),
						isLoading = false
					)
				}
			}
		)
	}
}
