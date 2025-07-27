package com.sapuseven.untis.ui.pages.timetable.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapuseven.untis.api.model.untis.Person
import com.sapuseven.untis.api.model.untis.absence.StudentAbsence
import com.sapuseven.untis.api.model.untis.timetable.Period
import com.sapuseven.untis.api.model.untis.timetable.PeriodData
import com.sapuseven.untis.data.repository.TimetableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AbsenceCheckViewModel @Inject constructor(
	private val timetableRepository: TimetableRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow(AbsenceCheckUiState())
	val uiState: StateFlow<AbsenceCheckUiState> = _uiState.asStateFlow()

	fun show(
		period: Period,
		initialPeriodData: PeriodData,
		studentData: Set<Person>
	) {
		_uiState.update {
			AbsenceCheckUiState(
				visible = true,
				period = period,
				periodData = initialPeriodData,
				studentData = studentData,
				detailedPerson = null,
				error = null
			)
		}
	}

	fun hide() {
		_uiState.update { it.copy(visible = false) }
	}

	fun showDetailed(student: Person) {
		_uiState.update { state ->
			state.copy(
				detailedPerson = student,
				newAbsenceStart = state.period?.startDateTime ?: LocalDateTime.now(),
				newAbsenceEnd = state.period?.endDateTime ?: LocalDateTime.now().plusHours(1)
			)
		}
	}

	fun hideDetailed() {
		_uiState.update { it.copy(detailedPerson = null) }
	}

	fun createAbsence(studentId: Long) = viewModelScope.launch {
		_uiState.update { it.copy(loadingStudents = it.loadingStudents + studentId, error = null) }
		timetableRepository.postAbsence(
			periodId = _uiState.value.period!!.id,
			studentId = studentId,
			startTime = _uiState.value.newAbsenceStart.toLocalTime(),
			endTime = _uiState.value.newAbsenceEnd.toLocalTime()
		)
			.onSuccess { newAbsences ->
				_uiState.update {
					it.copy(
						loadingStudents = it.loadingStudents - studentId,
						periodData = it.periodData!!.copy(
							absences = it.periodData.absences.orEmpty() + newAbsences
						)
					)
				}
			}
			.onFailure { throwable ->
				_uiState.update {
					it.copy(
						loadingStudents = it.loadingStudents - studentId,
						error = throwable.message
					)
				}
			}
	}

	fun deleteAbsence(studentId: Long, absenceId: Long) = viewModelScope.launch {
		_uiState.update { it.copy(loadingStudents = it.loadingStudents + studentId, error = null) }
		timetableRepository.deleteAbsence(absenceId)
			.onSuccess {
				_uiState.update {
					it.copy(
						loadingStudents = it.loadingStudents - studentId,
						periodData = it.periodData!!.copy(
							absences = it.periodData.absences.orEmpty().filterNot { absence ->
								absence.id == absenceId && absence.studentId == studentId
							}
						)
					)
				}
			}
			.onFailure { throwable ->
				_uiState.update {
					it.copy(loadingStudents = it.loadingStudents - studentId, error = throwable.message)
				}
			}
	}

	fun submitAbsencesChecked() = viewModelScope.launch {
		_uiState.update { it.copy(loading = true, error = null) }
		timetableRepository.postAbsencesChecked(setOf(_uiState.value.period!!.id))
			.onSuccess {
				_uiState.update {
					it.copy(
						loading = false,
						periodData = it.periodData!!.copy(absenceChecked = true)
					)
				}
				hide()
			}
			.onFailure { thr ->
				_uiState.update {
					it.copy(loading = false, error = thr.message)
				}
			}
	}

	fun setNewAbsenceStartTime(time: LocalTime) {
		_uiState.update {
			it.copy(newAbsenceStart = LocalDateTime.of(it.newAbsenceStart.toLocalDate(), time))
		}
	}

	fun setNewAbsenceEndTime(time: LocalTime) {
		_uiState.update {
			it.copy(newAbsenceEnd = LocalDateTime.of(it.newAbsenceEnd.toLocalDate(), time))
		}
	}
}

data class AbsenceCheckUiState(
	// Absence overview
	val visible: Boolean = false,
	val period: Period? = null,
	val periodData: PeriodData? = null,
	val studentData: Set<Person> = emptySet(),
	val loading: Boolean = false, // global loading state for the whole absence check
	val loadingStudents: Set<Long> = emptySet(), // loading state for individual students
	val error: String? = null, // unused

	// Detailed absence check for a single student
	val detailedPerson: Person? = null,
	val newAbsenceStart: LocalDateTime = LocalDateTime.now(),
	val newAbsenceEnd: LocalDateTime = LocalDateTime.now(),
) {
	val studentsForPeriod: List<Person>
		get() = periodData?.studentIds?.let { studentIds ->
			studentIds
				.mapNotNull { studentId -> studentData.find { it.id == studentId } }
				.sortedBy { it.fullName() }
		} ?: emptyList()

	fun existingAbsenceFor(studentId: Long): StudentAbsence? =
		periodData?.absences?.findLast { it.studentId == studentId }
}
