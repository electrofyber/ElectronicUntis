package com.sapuseven.untis.feature.timetable.details
/*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sapuseven.untis.R
import com.sapuseven.untis.core.api.model.untis.Person
import com.sapuseven.untis.core.api.model.untis.timetable.Period
import com.sapuseven.untis.core.api.model.untis.timetable.PeriodData
import com.sapuseven.untis.data.repository.TimetableRepository
import com.sapuseven.untis.ui.animations.fullscreenDialogAnimationEnter
import com.sapuseven.untis.ui.animations.fullscreenDialogAnimationExit
import com.sapuseven.untis.core.ui.common.SmallCircularProgressIndicator
import com.sapuseven.untis.core.ui.dialogs.TimePickerDialog
import com.sapuseven.untis.core.ui.functional.insetsPaddingValues
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class AbsenceCheckState(
	var studentData: Set<Person>,
	val timetableRepository: TimetableRepository,
	val scope: CoroutineScope,
	private val onPeriodDataUpdate: (PeriodData) -> Unit
) {
	private var _visible by mutableStateOf(false)
	val visible: Boolean
		get() = _visible

	private var _period: Period? by mutableStateOf(null)

	private var _periodData: PeriodData? by mutableStateOf(null)
	val periodData: PeriodData?
		get() = _periodData

	private var _detailedPerson: Person? by mutableStateOf(null)
	val detailedPerson: Person?
		get() = _detailedPerson

	internal var newAbsenceStart: LocalDateTime? by mutableStateOf(null)
	internal var newAbsenceEnd: LocalDateTime? by mutableStateOf(null)

	fun show(period: Period, periodData: PeriodData) {
		_period = period
		_periodData = periodData
		_visible = true
	}

	fun hide() {
		hideDetailed()
		_visible = false
	}

	fun showDetailed(student: Person) {
		newAbsenceStart = _period?.startDateTime
		newAbsenceEnd = _period?.endDateTime
		_detailedPerson = student
	}

	fun hideDetailed() {
		_detailedPerson = null
	}

	private fun updatePeriodData(periodData: PeriodData?) {
		periodData?.let {
			_periodData = it
			onPeriodDataUpdate(it)
		}
	}

	suspend fun createAbsence(
		studentId: Long,
		periodId: Long = _period!!.id,
		startTime: LocalTime = _period!!.startDateTime.toLocalTime(),
		endTime: LocalTime = _period!!.endDateTime.toLocalTime()
	) {
		timetableRepository.postAbsence(periodId, studentId, startTime, endTime).onSuccess { newAbsences ->
			updatePeriodData(_periodData?.let { it.copy(absences = (it.absences ?: emptyList()).plus(newAbsences)) })
		}
	}

	suspend fun deleteAbsence(absenceId: Long) {
		timetableRepository.deleteAbsence(absenceId).onSuccess {
			updatePeriodData(_periodData?.let { it.copy(absences = (it.absences ?: emptyList()).filterNot { it.id == absenceId }) })
		}
	}

	suspend fun submitAbsencesChecked() {
		timetableRepository.postAbsencesChecked(setOf(_period!!.id)).onSuccess {
			updatePeriodData(_periodData?.copy(absenceChecked = true))
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AbsenceCheck(
	modifier: Modifier = Modifier,
	viewModel: AbsenceCheckViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	BackHandler(
		enabled = uiState.visible,
	) {
		viewModel.hide()
	}

	AnimatedVisibility(
		visible = uiState.visible,
		enter = fullscreenDialogAnimationEnter(),
		exit = fullscreenDialogAnimationExit()
	) {
		LazyColumn(
			modifier = modifier,
			contentPadding = insetsPaddingValues()
		) {
			items(uiState.studentsForPeriod) { student ->
				val existingAbsence = uiState.existingAbsenceFor(student.id)

				ListItem(
					headlineContent = {
						Text(text = student.fullName())
					},
					supportingContent = existingAbsence?.let {
						{
							Text(text = it.text)
						}
					},
					leadingContent = {
						if (uiState.loadingStudents.contains(student.id))
							SmallCircularProgressIndicator()
						else if (existingAbsence != null)
							Icon(
								painterResource(id = R.drawable.all_cross),
								contentDescription = "Absent"
							)
						else
							Icon(
								painterResource(id = R.drawable.all_check),
								contentDescription = "Present"
							)
					},
					modifier = Modifier.combinedClickable(
						onClick = {
							existingAbsence?.let {
								viewModel.deleteAbsence(student.id, it.id)
							} ?: let {
								viewModel.createAbsence(student.id)
							}
						},
						onLongClick = {
							viewModel.showDetailed(student)
						}
					),
					/*trailingContent = {
						IconButton(
							onClick = {
								params?.let {
									detailedAbsenceCheck =
										(it.periodDataId to student) to (it.startDateTime to it.endDateTime)
								}
							}
						) {
							Icon(
								painter = painterResource(id = R.drawable.notification_clock),
								contentDescription = null
							)
						}
					}*/
				)
			}
		}
	}

	AnimatedVisibility(
		visible = uiState.detailedPerson != null,
		enter = fullscreenDialogAnimationEnter(),
		exit = fullscreenDialogAnimationExit()
	) {
		BackHandler(
			enabled = uiState.detailedPerson != null,
		) {
			viewModel.hideDetailed()
		}

		Column(
			modifier = modifier
		) {
			var showStartTimePicker by remember { mutableStateOf(false) }
			var showEndTimePicker by remember { mutableStateOf(false) }

			ListItem(
				modifier = Modifier.clickable {
					showStartTimePicker = true
				},
				headlineContent = {
					Text(text = "Absence start time") // TODO Extract string resource
				},
				trailingContent = {
					Text(
						text = uiState.newAbsenceStart.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
						style = MaterialTheme.typography.labelLarge
					)
				}
			)

			ListItem(
				modifier = Modifier.clickable {
					showEndTimePicker = true
				},
				headlineContent = {
					Text(text = "Absence end time") // TODO Extract string resource
				},
				trailingContent = {
					Text(
						text = uiState.newAbsenceEnd.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
						style = MaterialTheme.typography.labelLarge
					)
				}
			)

			if (showStartTimePicker) {
				TimePickerDialog(
					initialSelection = uiState.newAbsenceStart.toLocalTime(),
					onDismiss = {
						showStartTimePicker = false
					}
				) { time ->
					viewModel.setNewAbsenceStartTime(time)
					showStartTimePicker = false
				}
			}

			if (showEndTimePicker) {
				TimePickerDialog(
					initialSelection = uiState.newAbsenceEnd.toLocalTime(),
					onDismiss = {
						showEndTimePicker = false
					}
				) { time ->
					viewModel.setNewAbsenceEndTime(time)
					showEndTimePicker = false
				}
			}
		}
	}
}
*/
