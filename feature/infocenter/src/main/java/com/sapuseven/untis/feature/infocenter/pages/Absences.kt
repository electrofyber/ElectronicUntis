package com.sapuseven.untis.feature.infocenter.pages

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.sapuseven.untis.core.model.absences.Absence
import com.sapuseven.untis.core.model.absences.Excuse
import com.electrofyber.untis.feature.infocenter.R
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun InfoCenterAbsences(uiState: AbsencesUiState) {
	Crossfade(targetState = uiState, label = "InfoCenter Absences Content") { state ->
		when (state) {
			AbsencesUiState.Loading -> InfoCenterLoading()
			AbsencesUiState.Hidden -> {}
			is AbsencesUiState.Success -> {
				state.absences.fold(
					onSuccess = { absences ->
						if (state.isEmpty) {
							Text(
								text = stringResource(R.string.feature_infocenter_absences_empty),
								textAlign = TextAlign.Center,
								modifier = Modifier.fillMaxWidth()
							)
						} else {
							LazyColumn(
								horizontalAlignment = Alignment.CenterHorizontally,
								modifier = Modifier.fillMaxSize()
							) {
								items(absences) {
									AbsenceItem(it)
								}
							}
						}
					},
					onFailure = { InfoCenterError(it) }
				)
			}
		}
	}
}

@Composable
private fun AbsenceItem(item: Absence) {
	ListItem(
		headlineContent = {
			Text(
				item.startDateTime.toJavaLocalDateTime()
					.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
			)
		},
		supportingContent = {
			Column {
				// Time range
				Text(
					stringResource(
						R.string.feature_infocenter_absences_timerange,
						item.startDateTime.toJavaLocalDateTime()
							.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
						item.endDateTime.toJavaLocalDateTime()
							.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
					)
				)

				// Reason
				Text(
					stringResource(
						R.string.feature_infocenter_absence_reason,
						if (item.absenceReason.isNotEmpty())
							item.absenceReason.replaceFirstChar { it.uppercase() }
						else
							stringResource(R.string.feature_infocenter_absence_reason_unknown)
					)
				)

				// Additional Text
				if (item.text.isNotBlank()) {
					Text(item.text)
				}

				// Excuse status
				val excused = item.excuse?.excused ?: false
				item.excuse?.text?.let {
					Text(
						stringResource(
							if (excused)
								R.string.feature_infocenter_absence_excused_status
							else
								R.string.feature_infocenter_absence_unexcused_status,
							it
						)
					)
				} ?: Text(
					if (excused)
						stringResource(R.string.feature_infocenter_absence_excused)
					else
						stringResource(R.string.feature_infocenter_absence_unexcused)
				)
			}
		}
	)
}

sealed interface AbsencesUiState {
	data object Loading : AbsencesUiState

	data object Hidden : AbsencesUiState

	data class Success(
		val absences: Result<List<Absence>>,
		val excuses: List<Excuse>
	) : AbsencesUiState {
		val isEmpty: Boolean get() = absences.getOrDefault(emptyList()).isEmpty()
	}
}
