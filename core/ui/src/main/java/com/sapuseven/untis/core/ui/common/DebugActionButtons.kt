package com.sapuseven.untis.core.ui.common

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.sapuseven.untis.core.api.model.untis.timetable.PeriodData
import com.sapuseven.untis.core.model.Period
import com.sapuseven.untis.core.ui.R
import kotlinx.coroutines.launch

@Composable
internal fun DebugInfoAction(
	title: @Composable (() -> Unit)? = null,
	content: @Composable (() -> Unit)? = null,
) {
	var showInfoDialog by remember { mutableStateOf(false) }

	IconButton(onClick = { showInfoDialog = true }) {
		Icon(
			painterResource(R.drawable.core_ui_debug),
			contentDescription = "Debug info"
		)
	}

	if (showInfoDialog) {
		AlertDialog(
			onDismissRequest = { showInfoDialog = false },
			title = title,
			text = content,
			confirmButton = {
				TextButton(
					onClick = { showInfoDialog = false }) {
					Text(stringResource(R.string.core_ui_button_ok))
				}
			}
		)
	}
}

@Composable
fun DebugDisclaimerAction(
	colorSchemeDebugInfo: String
) {
	DebugInfoAction(
		title = { Text("Debug information") }
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier.verticalScroll(rememberScrollState())
		) {
			Text(
				"You are running a debug build of the app.\n\n" +
					"This means that the app is not optimized and you will see some additional settings and functions.\n" +
					"It is only recommended to use this variant when developing or gathering information about specific issues.\n" +
					"For normal daily use, you should switch to a stable release build of the app.\n\n" +
					"Please remember that diagnostic data may include personal details, " +
					"so it is your responsibility to check and obfuscate any gathered data before uploading."
			)
			Text(style = MaterialTheme.typography.titleLarge, text = "Debug Data")
			Text(style = MaterialTheme.typography.titleMedium, text = "ColorScheme")
			RawText(
				item = colorSchemeDebugInfo,
				encode = false
			)
		}
	}
}

//@Serializable
private data class DebugPeriodInfo(
	val period: Period,
	val periodData: PeriodData?
)

@Composable
fun DebugTimetableItemDetailsAction(
	timetablePeriods: List<Period>,
	periodDataMap: Map<Long, PeriodData>
) {
	DebugInfoAction(
		title = { Text("Raw lesson details") }
	) {
		LazyColumn(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier
				.fillMaxWidth()
		) {
			items(timetablePeriods) {
				RawText(item = DebugPeriodInfo(it, periodDataMap[it.id]))
			}
		}
	}
}

@Composable
private inline fun <reified T> RawText(item: T, encode: Boolean = true) {
	val clipboard: Clipboard = LocalClipboard.current
	val scope = rememberCoroutineScope()
	val itemText = remember { /* TODO if (encode) json.encodeToString(item) else*/ item.toString() }

	Column(
		horizontalAlignment = Alignment.End,
		modifier = Modifier
			.clip(RoundedCornerShape(8.dp))
			.background(MaterialTheme.colorScheme.background)
			.padding(8.dp)
	) {
		Text(
			color = MaterialTheme.colorScheme.onSurface,
			fontFamily = FontFamily.Monospace,
			text = itemText,
			modifier = Modifier.horizontalScroll(rememberScrollState())
		)
		TextButton(
			onClick = {
				scope.launch {
					clipboard.setClipEntry(
						ClipEntry(
							ClipData.newPlainText(
								"BetterUntis debug info",
								AnnotatedString(itemText)
							)
						)
					)
				}
			}
		) {
			Icon(
				painter = painterResource(R.drawable.core_ui_copy),
				contentDescription = stringResource(R.string.core_ui_button_copy),
				modifier = Modifier
					.padding(end = 8.dp)
			)
			Text("Copy")
		}
	}
}
