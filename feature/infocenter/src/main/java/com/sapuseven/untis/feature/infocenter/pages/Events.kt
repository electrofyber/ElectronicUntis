package com.sapuseven.untis.feature.infocenter.pages

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sapuseven.untis.core.model.timetable.Exam
import com.sapuseven.untis.core.model.timetable.Homework
import com.sapuseven.untis.feature.infocenter.R
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
fun InfoCenterEvents(uiState: EventsUiState) {
	Crossfade(targetState = uiState, label = "InfoCenter Events Content") { state ->
		LazyColumn(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.fillMaxSize()
		) {
			item {
				Text(
					text = stringResource(R.string.feature_infocenter_exam),
					style = MaterialTheme.typography.labelLarge,
					modifier = Modifier.padding(bottom = 8.dp)
				)
			}

			when (state) {
				EventsUiState.Loading -> item { InfoCenterLoading() }
				is EventsUiState.Success -> {
					state.exams.fold(
						onSuccess = {
							if (state.isExamsEmpty) item {
								Text(
									text = stringResource(R.string.feature_infocenter_exams_empty),
									textAlign = TextAlign.Center,
									modifier = Modifier.fillMaxWidth()
								)
							} else {
								items(it) { item -> ExamItem(item) }
							}
						},
						onFailure = { item { InfoCenterError(it) } }
					)
				}
			}

			item {
				Text(
					text = stringResource(R.string.feature_infocenter_homework),
					style = MaterialTheme.typography.labelLarge,
					modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
				)
			}

			when (state) {
				EventsUiState.Loading -> item { InfoCenterLoading() }
				is EventsUiState.Success -> {
					state.homework.fold(
						onSuccess = {
							if (state.isHomeworkEmpty) item {
								Text(
									text = stringResource(R.string.feature_infocenter_homework_empty),
									textAlign = TextAlign.Center,
									modifier = Modifier.fillMaxWidth()
								)
							}
							else {
								items(it) { item -> HomeworkItem(item) }
							}
						},
						onFailure = { item { InfoCenterError(it) } }
					)
				}
			}
		}
	}
}

@Composable
private fun ExamItem(item: Exam) {
	val subject = item.subject!!.shortName // TODO handle null case
	ListItem(
		overlineContent = {
			Text(formatExamTime(item.startDateTime, item.endDateTime))
		},
		headlineContent = {
			Text(item.name?.takeIf { it.contains(subject) }
				?: stringResource(
					R.string.feature_infocenter_events_exam_name_long,
					subject,
					item.name ?: subject
				)
			)
		}
	)
}

@Composable
private fun HomeworkItem(item: Homework) {
	ListItem(
		overlineContent = @Composable {
			Text(
				item.endDate.toJavaLocalDate()
					.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
			)
		},
		headlineContent = {
			item.subject?.let {
				Text(it.longName)
			}
		},
		supportingContent = if (item.text.isNotBlank()) {
			{ Text(item.text) }
		} else null
	)
}

@Composable
private fun formatExamTime(startDateTime: LocalDateTime, endDateTime: LocalDateTime): String {
	return stringResource(
		if (startDateTime.dayOfYear == endDateTime.dayOfYear)
			R.string.feature_infocenter_timeformat_sameday
		else
			R.string.feature_infocenter_timeformat,
		startDateTime.toJavaLocalDateTime()
			.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
		startDateTime.toJavaLocalDateTime()
			.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
		endDateTime.toJavaLocalDateTime()
			.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
		endDateTime.toJavaLocalDateTime()
			.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
	)
}

sealed interface EventsUiState {
	data object Loading : EventsUiState

	data class Success(
		val exams: Result<List<Exam>>,
		val homework: Result<List<Homework>>
	) : EventsUiState {
		constructor(exams: List<Exam>, homework: List<Homework>) : this(
			Result.success(exams),
			Result.success(homework)
		)

		val isExamsEmpty: Boolean get() = exams.getOrDefault(emptyList()).isEmpty()
		val isHomeworkEmpty: Boolean get() = homework.getOrDefault(emptyList()).isEmpty()
	}
}
