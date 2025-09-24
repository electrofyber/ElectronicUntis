package com.sapuseven.untis.feature.timetable.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapuseven.untis.core.domain.cache.FromCache
import com.sapuseven.untis.core.domain.repository.MasterDataRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.domain.timetable.GetTimetableUseCase
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementKey
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.model.timetable.Period
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PeriodDetailsViewModel.Factory::class)
class PeriodDetailsViewModel @AssistedInject constructor(
	@Assisted("id") private val id: Long,
	@Assisted("type") private val type: ElementType,
	@Assisted("page") private val page: Int,
	@Assisted("periodIds") private val periodIds: List<Long>,
	@Assisted("initialPeriod") private val initialPeriod: Int,
	private val userRepository: UserRepository,
	private val masterDataRepository: MasterDataRepository,
	private val getTimetable: GetTimetableUseCase,
) : ViewModel() {
	@AssistedFactory
	interface Factory {
		fun create(
			@Assisted("id") id: Long,
			@Assisted("type") type: ElementType,
			@Assisted("page") page: Int,
			@Assisted("periodIds") periodIds: List<Long>,
			@Assisted("initialPeriod") initialPeriod: Int
		): PeriodDetailsViewModel
	}

	private val _uiState = MutableStateFlow(
		PeriodDetailsUiState(
			periods = periodIds.map { null },
			initialPeriod = initialPeriod,
		)
	)
	val uiState: StateFlow<PeriodDetailsUiState> = _uiState

	init {
		viewModelScope.launch {
			val element = ElementKey(id, type).let { masterDataRepository.getElement(it) ?: Element.basic(it) }

			val timetable = getTimetable(
				user = userRepository.getActiveUser(),
				element = element,
				page = page,
				fromCache = FromCache.ONLY
			).first()

			val periods = periodIds.map { periodId ->
				timetable.periods.find { it.id == periodId }
			}

			_uiState.update { it.copy(periods = periods) }
		}
	}

	/*val allElements = masterDataRepository.timetableElements
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
	}*/
}

/*data class TimetableItemUiState(
	val periodDataMap: Map<Long, PeriodData> = emptyMap(),
	val studentData: Set<Person> = emptySet(),
	val newLessonTopics: Map<Long, String?> = emptyMap(),
)

data class LessonTopicUiState(
	val periodId: Long? = null,
	val lessonTopic: String = "",
	val loading: Boolean = false,
	val error: StringResourceDescriptor? = null
)*/

data class PeriodDetailsUiState(
	val periods: List<Period?>,
	val initialPeriod: Int,
)
