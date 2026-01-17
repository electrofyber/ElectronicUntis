package com.sapuseven.untis.feature.roomfinder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sapuseven.untis.core.model.roomfinder.RoomFinderItemData
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.ui.animation.fullscreenDialogAnimationEnter
import com.sapuseven.untis.core.ui.animation.fullscreenDialogAnimationExit
import com.sapuseven.untis.core.ui.common.SmallCircularProgressIndicator
import com.sapuseven.untis.core.ui.dialogs.ElementPickerDialogFullscreen
import com.sapuseven.untis.core.ui.functional.None
import com.sapuseven.untis.core.ui.functional.bottomInsets
import kotlinx.datetime.toJavaLocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomFinderScreen(
	viewModel: RoomFinderViewModel = hiltViewModel(),
	onBackClick: () -> Unit,
	onRoomClick: (id: Long) -> Unit,
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	var showElementPicker by rememberSaveable { mutableStateOf(false) }

	Scaffold(
		modifier = Modifier.bottomInsets(), topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(stringResource(id = R.string.feature_roomfinder_title))
				},
				navigationIcon = {
					IconButton(onClick = { onBackClick() }) {
						Icon(
							imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
							contentDescription = stringResource(id = com.sapuseven.untis.core.ui.R.string.all_back)
						)
					}
				},
			)
		}, floatingActionButton = {
			FloatingActionButton(
				//modifier = Modifier.bottomInsets(),
				containerColor = MaterialTheme.colorScheme.primary,
				onClick = { showElementPicker = true }) {
				Icon(
					imageVector = Icons.Outlined.Add,
					contentDescription = stringResource(id = com.sapuseven.untis.core.ui.R.string.all_add)
				)
			}
		}, bottomBar = {
			AnimatedVisibility(
				visible = uiState.roomList.isNotEmpty(),
				enter = fadeIn() + expandVertically(),
				exit = fadeOut() + shrinkVertically()
			) {
				RoomFinderHourSelector(uiState.hourState) {
					viewModel.selectHour(it)
				}
			}
		}, contentWindowInsets = WindowInsets.None
	) { innerPadding ->
		Column(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center,
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f)
			) {
				LazyColumn(
					Modifier
						.fillMaxWidth()
						.weight(1f)
				) {
					items(
						uiState.roomList, key = { it.room.elementId }) {
						RoomListItem(
							element = uiState.elements[ElementType.ROOM]?.find { element -> element.id == it.room.elementId },
							itemData = it,
							hourState = uiState.hourState,
							onDelete = { viewModel.deleteRoom(it.room) },
							modifier = Modifier
								.animateItem()
								.clickable { onRoomClick(it.room.elementId) })
					}
				}

				if (uiState.roomList.isEmpty()) RoomFinderListEmpty(
					modifier = Modifier
						.align(Alignment.CenterHorizontally)
						.weight(1f)
				)
			}
		}
	}

	AnimatedVisibility(
		visible = showElementPicker,
		enter = fullscreenDialogAnimationEnter(),
		exit = fullscreenDialogAnimationExit()
	) {
		ElementPickerDialogFullscreen(
			title = { Text(stringResource(id = com.sapuseven.untis.core.ui.R.string.all_add)) }, // TODO: Proper string resource
			multiSelect = true,
			hideTypeSelection = true,
			initialType = ElementType.ROOM,
			onDismiss = { showElementPicker = false },
			onMultiSelect = { viewModel.addRooms(it) },
			elements = uiState.elements
		)
	}
}

@Composable
fun RoomFinderListEmpty(modifier: Modifier = Modifier) {
	val annotatedString = buildAnnotatedString {
		val text = stringResource(R.string.feature_roomfinder_no_rooms)
		append(text.substringBefore("+"))
		appendInlineContent(id = "add")
		append(text.substringAfter("+"))
	}

	val inlineContentMap = mapOf(
		"add" to InlineTextContent(
			Placeholder(
				MaterialTheme.typography.bodyLarge.fontSize,
				MaterialTheme.typography.bodyLarge.fontSize,
				PlaceholderVerticalAlign.TextCenter
			)
		) {
			Icon(
				imageVector = Icons.Outlined.Add,
				modifier = Modifier.fillMaxSize(),
				contentDescription = "+"
			)
		})

	Text(
		text = annotatedString,
		textAlign = TextAlign.Center,
		inlineContent = inlineContentMap,
		modifier = modifier
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomFinderHourSelector(
	hourState: HourState, onSelectionChange: (Int?) -> Unit
) = with(hourState) {
	hours[selectedIndex].let { hour ->
		ListItem(headlineContent = {
			Text(
				text = stringResource(
					id = R.string.feature_roomfinder_current_hour,
					hour.day.dayOfWeek.getDisplayName(
						TextStyle.FULL_STANDALONE, Locale.getDefault()
					),
					hour.unit.label
				), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
			)
		}, supportingContent = {
			Text(
				text = stringResource(
					id = R.string.feature_roomfinder_current_hour_time,
					hour.unit.startTime.toJavaLocalTime()
						.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
					hour.unit.endTime.toJavaLocalTime()
						.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
				), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
			)
		}, leadingContent = {
			IconButton(
				enabled = selectedIndex > 0, onClick = { onSelectionChange(selectedIndex - 1) }) {
				Icon(
					painter = painterResource(id = R.drawable.feature_roomfinder_previous),
					contentDescription = stringResource(id = R.string.feature_roomfinder_image_previous_hour)
				)
			}
		}, trailingContent = {
			IconButton(
				enabled = selectedIndex < hours.lastIndex,
				onClick = { onSelectionChange(selectedIndex + 1) }) {
				Icon(
					painter = painterResource(id = R.drawable.feature_roomfinder_next),
					contentDescription = stringResource(id = R.string.feature_roomfinder_image_next_hour)
				)
			}
		}, modifier = Modifier.clickable { onSelectionChange(null) })
	}
}

@Composable
fun RoomListItem(
	element: Element?,
	itemData: RoomFinderItemData,
	hourState: HourState,
	modifier: Modifier = Modifier,
	onDelete: (() -> Unit)? = null,
) {
	// Potential improvement: handle loading errors

	val state = itemData.freeHoursAt(hourState.selectedIndex)

	val currentDay = hourState.selected.day
	val hoursLeftInDay =
		hourState.hours.drop(hourState.selectedIndex).count { it.day == currentDay }
	val hoursLeftInWeek = hourState.hours.drop(hourState.selectedIndex).count()

	val isLoading = itemData.states.isEmpty()
	val isFree = !isLoading && state > 0
	val isOccupied = !isLoading && state == 0

	ListItem(
		headlineContent = { Text(element?.longName ?: itemData.room.elementId.toString()) },
		supportingContent = {
			Text(
				when {
					isLoading -> stringResource(R.string.feature_roomfinder_loading_data)
					isOccupied -> stringResource(R.string.feature_roomfinder_item_desc_occupied)
					state >= hoursLeftInWeek -> stringResource(R.string.feature_roomfinder_item_desc_free_week)
					state >= hoursLeftInDay -> stringResource(R.string.feature_roomfinder_item_desc_free_day)
					isFree -> pluralStringResource(
						R.plurals.feature_roomfinder_item_desc, state, state
					)

					else -> stringResource(R.string.feature_roomfinder_error)
				}
			)
		},
		leadingContent = {
			if (isLoading) {
				SmallCircularProgressIndicator()
			} else {
				Icon(
					painter = painterResource(
						id = when {
							isOccupied -> R.drawable.feature_roomfinder_cross
							isFree -> R.drawable.feature_roomfinder_check
							else -> R.drawable.feature_roomfinder_error
						}
					),
					tint = when {
						isOccupied -> MaterialTheme.colorScheme.error
						isFree -> MaterialTheme.colorScheme.primary
						else -> LocalContentColor.current
					},
					contentDescription = stringResource(id = R.string.feature_roomfinder_image_availability_indicator)
				)
			}
		},
		trailingContent = onDelete?.let {
			{
				IconButton(onClick = onDelete) {
					Icon(
						imageVector = Icons.Outlined.Delete,
						contentDescription = stringResource(id = R.string.feature_roomfinder_delete_item)
					)
				}
			}
		},
		modifier = modifier
	)
}
