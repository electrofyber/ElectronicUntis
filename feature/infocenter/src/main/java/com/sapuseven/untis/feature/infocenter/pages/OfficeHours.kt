package com.sapuseven.untis.feature.infocenter.pages

import androidx.compose.animation.Crossfade
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
import com.sapuseven.untis.core.model.officehours.OfficeHour
import com.sapuseven.untis.feature.infocenter.R
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun InfoCenterOfficeHours(uiState: OfficeHoursUiState) {
	Crossfade(targetState = uiState, label = "InfoCenter Office Hours Content") { state ->
		when (state) {
			OfficeHoursUiState.Loading -> InfoCenterLoading()
			OfficeHoursUiState.Hidden -> {}
			is OfficeHoursUiState.Success -> {
				state.officeHours.fold(
					onSuccess = { officeHours ->
						if (state.isEmpty) {
							Text(
								text = stringResource(R.string.feature_infocenter_officehours_empty),
								textAlign = TextAlign.Center,
								modifier = Modifier.fillMaxWidth()
							)
						} else {
							LazyColumn(
								horizontalAlignment = Alignment.CenterHorizontally,
								modifier = Modifier.fillMaxSize()
							) {
								items(officeHours) {
									OfficeHourItem(it)
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
private fun OfficeHourItem(item: OfficeHour) {
	val body = listOf(
		item.displayNameRooms,
		item.registrationInfo.phone,
		item.registrationInfo.email
	).filter { it?.isNotEmpty() == true }.joinToString("\n")

	ListItem(
		overlineContent = {
			Text(
				formatOfficeHourTime(item.startDateTime, item.endDateTime)
			)
		},
		headlineContent = { Text(item.teacher.longName) },
		supportingContent = if (body.isNotBlank()) {
			{ Text(body) }
		} else null
	)
}

@Composable
private fun formatOfficeHourTime(
	startDateTime: LocalDateTime,
	endDateTime: LocalDateTime,
): String {
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

sealed interface OfficeHoursUiState {
	data object Loading : OfficeHoursUiState

	data object Hidden : OfficeHoursUiState

	data class Success(val officeHours: Result<List<OfficeHour>>) : OfficeHoursUiState {
		val isEmpty: Boolean get() = officeHours.getOrDefault(emptyList()).isEmpty()
	}
}
