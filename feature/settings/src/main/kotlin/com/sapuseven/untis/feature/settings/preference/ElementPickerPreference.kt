package com.sapuseven.untis.feature.settings.preference

import ElementItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.google.protobuf.MessageLite
import com.sapuseven.compose.protostore.data.SettingsDataSource
import com.sapuseven.compose.protostore.ui.preferences.Preference
import com.sapuseven.untis.core.datastore.model.TimetableElement
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.ui.dialogs.ElementPickerDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun <Model : MessageLite, ModelBuilder : MessageLite.Builder> ElementPickerPreference(
	title: (@Composable () -> Unit),
	summary: (@Composable () -> Unit)? = null,
	leadingContent: (@Composable () -> Unit)? = null,
	settingsDataSource: SettingsDataSource<Model, ModelBuilder>,
	value: (Model) -> TimetableElement,
	scope: CoroutineScope = rememberCoroutineScope(),
	enabledCondition: (Model) -> Boolean = { true },
	highlight: Boolean = false,
	onValueChange: (ModelBuilder.(value: TimetableElement) -> Unit)? = null,
	elements: Map<ElementType, List<Element>>,
) {
	val selectedTypeState: State<ElementType?> = remember {
		settingsDataSource.getSettings().map { settings ->
			ElementType.entries.firstOrNull { it.ordinal == value(settings).elementType }
		}
	}.collectAsState(initial = null)
	var showDialog by remember { mutableStateOf(false) }

	Preference(
		title = title,
		summary = summary,
		supportingContent = { currentValue, _ ->
			currentValue.toElementEntity(elements)?.let {
				ElementItem(it) { shortName, _, _ ->
					Text(shortName)
				}
			}
		},
		leadingContent = leadingContent,
		settingsDataSource = settingsDataSource,
		value = value,
		scope = scope,
		enabledCondition = enabledCondition,
		highlight = highlight,
		onClick = {
			showDialog = true;
		}
	)

	if (showDialog)
		ElementPickerDialog(
			title = title,
			elements = elements,
			onDismiss = {
				showDialog = false
			},
			onSelect = { element ->
				showDialog = false
				scope.launch {
					settingsDataSource.updateSettings {
						onValueChange?.invoke(
							this,
							element?.toTimetableElement() ?: TimetableElement.getDefaultInstance()
						)
					}
				}
			},
			initialType = selectedTypeState.value
		)
}

fun TimetableElement.toElementEntity(elements: Map<ElementType, List<Element>>): Element? =
	elements[ElementType.entries.firstOrNull { it.ordinal == elementType }]?.firstOrNull { it.id == elementId }

fun Element.toTimetableElement(): TimetableElement =
	TimetableElement.newBuilder().apply {
		elementId = id
		elementType = type.ordinal
	}.build()
