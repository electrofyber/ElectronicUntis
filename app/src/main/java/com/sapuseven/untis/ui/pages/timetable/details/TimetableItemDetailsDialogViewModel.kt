package com.sapuseven.untis.ui.pages.timetable.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapuseven.untis.R
import com.sapuseven.untis.core.api.model.untis.Person
import com.sapuseven.untis.core.api.model.untis.timetable.Period
import com.sapuseven.untis.core.api.model.untis.timetable.PeriodData
import com.sapuseven.untis.data.repository.MasterDataRepository
import com.sapuseven.untis.data.repository.TimetableRepository
import com.sapuseven.untis.data.repository.UserRepository
import com.sapuseven.untis.core.ui.functional.StringResourceDescriptor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimetableItemDetailsDialogViewModel @Inject constructor(
	val timetableRepository: TimetableRepository,
	private val userRepository: UserRepository,
	masterDataRepository: MasterDataRepository
) : ViewModel() {
	val allElements = masterDataRepository.timetableElements
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = emptyMap()
		)

	private val _uiState = MutableStateFlow(TimetableItemUiState())
	val uiState: StateFlow<TimetableItemUiState> = _uiState.asStateFlow()

	private val _lessonTopicUiState = MutableStateFlow(LessonTopicUiState())
	val lessonTopicUiState: StateFlow<LessonTopicUiState> = _lessonTopicUiState.asStateFlow()

	fun loadPeriodData(periods: Set<Period>) {
		viewModelScope.launch {
			timetableRepository.getPeriodData(periods)
				.catch { /* TODO handle error */ }
				.collect { result ->
					_uiState.update { state ->
						state.copy(
							periodDataMap = result.dataByTTId,
							studentData = result.referencedStudents.toSet(),
						)
					}
				}
		}
	}

	fun updatePeriodData(periodData: PeriodData) {
		_uiState.update {
			it.copy(
				periodDataMap = it.periodDataMap + (periodData.ttId to periodData)
			)
		}
	}

	fun setLessonTopic(lessonTopic: String) {
		_lessonTopicUiState.update {
			it.copy(
				lessonTopic = lessonTopic
			)
		}
	}

	fun submitLessonTopic(periodId: Long, lessonTopic: String) {
		viewModelScope.launch {
			_lessonTopicUiState.update { it.copy(loading = true, error = null) }

			timetableRepository.postLessonTopic(periodId, lessonTopic)
				.onSuccess { ok ->
					if (ok) {
						_uiState.update {
							it.copy(
								newLessonTopics = it.newLessonTopics + (periodId to lessonTopic)
							)
						}
						resetLessonTopicState()
					} else {
						_lessonTopicUiState.update {
							it.copy(
								loading = false,
								error = StringResourceDescriptor(R.string.errormessagedictionary_generic)
							)
						}
					}
				}
				.onFailure { throwable ->
					_lessonTopicUiState.update {
						it.copy(
							loading = false,
							error = StringResourceDescriptor(
								R.string.all_api_error_generic,
								throwable.message ?: "null"
							)
						)
					}
				}
		}
	}

	fun showLessonTopic(periodId: Long, initialLessonTopic: String = "") {
		_lessonTopicUiState.value = LessonTopicUiState(periodId, initialLessonTopic)
	}

	fun resetLessonTopicState() {
		_lessonTopicUiState.value = LessonTopicUiState()
	}
}

data class TimetableItemUiState(
	val periodDataMap: Map<Long, PeriodData> = emptyMap(),
	val studentData: Set<Person> = emptySet(),
	val newLessonTopics: Map<Long, String?> = emptyMap(),
)

data class LessonTopicUiState(
	val periodId: Long? = null,
	val lessonTopic: String = "",
	val loading: Boolean = false,
	val error: StringResourceDescriptor? = null
)
