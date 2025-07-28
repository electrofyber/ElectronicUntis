package com.sapuseven.untis.ui.pages.timetable.details

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sapuseven.untis.BuildConfig
import com.sapuseven.untis.R
import com.sapuseven.untis.core.api.model.untis.Attachment
import com.sapuseven.untis.core.api.model.untis.enumeration.PeriodRight
import com.sapuseven.untis.core.api.model.untis.timetable.PeriodData
import com.sapuseven.untis.models.PeriodElementEntity
import com.sapuseven.untis.models.PeriodItem
import com.sapuseven.untis.models.toLongString
import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.ui.common.AppScaffold
import com.sapuseven.untis.core.ui.common.ClickableUrlText
import com.sapuseven.untis.core.ui.common.DebugTimetableItemDetailsAction
import com.sapuseven.untis.core.ui.common.HorizontalPagerIndicator
import com.sapuseven.untis.core.ui.common.SmallCircularProgressIndicator
import com.sapuseven.untis.core.ui.dialogs.AttachmentsDialog
import com.sapuseven.untis.core.ui.dialogs.DynamicHeightAlertDialog
import com.sapuseven.untis.core.ui.functional.bottomInsets
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableItemDetailsDialog(
	periodItems: List<PeriodItem>,
	initialPage: Int = 0,
	onDismiss: (requestedElement: ElementEntity?) -> Unit,
	viewModel: TimetableItemDetailsDialogViewModel = hiltViewModel(),
	absenceCheckViewModel: AbsenceCheckViewModel = hiltViewModel()
) {
	var dismissed by rememberSaveable { mutableStateOf(false) }
	val pagerState = rememberPagerState(initialPage) { periodItems.size }
	val error by remember { mutableStateOf<Throwable?>(null) }

	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val absenceCheckUiState by absenceCheckViewModel.uiState.collectAsStateWithLifecycle()

	val absenceCheckViewModel: AbsenceCheckViewModel = hiltViewModel()

	fun dismiss(requestedElement: ElementEntity? = null) {
		onDismiss(requestedElement)
		dismissed = true
	}

	BackHandler(
		enabled = !dismissed,
	) {
		dismiss()
	}

	LaunchedEffect(Unit) {
		val periods = periodItems.map { it.originalPeriod }.toSet()
		Log.d("TimetableItemDetailsDlg", "Fetching period data for ${periods.map { it.id }}")
		viewModel.loadPeriodData(periods)
	}

	LaunchedEffect(absenceCheckUiState.periodData) {
		absenceCheckUiState.periodData?.let { updatedPeriodData ->
			viewModel.updatePeriodData(updatedPeriodData)
		}
	}

	AppScaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(
						absenceCheckUiState.detailedPerson?.fullName()
							?: stringResource(id = R.string.all_lesson_details)
					)
				},
				navigationIcon = {
					IconButton(onClick = {
						if (absenceCheckUiState.visible)
							absenceCheckViewModel.hide()
						else
							dismiss()
					}) {
						Icon(
							imageVector = Icons.Outlined.Close,
							contentDescription = stringResource(id = R.string.all_close)
						)
					}
				},
				actions = {
					if (BuildConfig.DEBUG)
						DebugTimetableItemDetailsAction(periodItems, uiState.periodDataMap)
				}
			)
		},
		floatingActionButton = {
			AnimatedVisibility(
				visible = absenceCheckUiState.visible,
				enter = scaleIn(),
				exit = scaleOut()
			) {
				FloatingActionButton(
					modifier = Modifier.bottomInsets(),
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = MaterialTheme.colorScheme.onPrimary,
					onClick = {
						absenceCheckUiState.detailedPerson?.let {
							absenceCheckViewModel.createAbsence(it.id)
							absenceCheckViewModel.hideDetailed()
						} ?: run {
							absenceCheckViewModel.submitAbsencesChecked()
						}
					}
				) {
					if (absenceCheckUiState.loading)
						SmallCircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
					else if (absenceCheckUiState.detailedPerson != null)
						Icon(
							painter = painterResource(id = R.drawable.all_check),
							contentDescription = stringResource(R.string.all_dialog_absences_save_detailed)
						)
					else
						Icon(
							painter = painterResource(id = R.drawable.all_save),
							contentDescription = stringResource(R.string.all_dialog_absences_save)
						)
				}
			}
		}
	) { innerPadding ->
		Box(
			modifier = Modifier
				.padding(innerPadding)
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier
					.fillMaxSize()
			) {
				HorizontalPager(
					state = pagerState,
					modifier = Modifier
						.weight(1f)
				) { page ->
					periodItems[page].also { periodItem ->
						TimetableItemDetailsDialogPage(
							periodItem = periodItem,
							periodData = uiState.periodDataMap[periodItem.originalPeriod.id],
							error = error,
							viewModel = viewModel,
							onElementClick = { dismiss(it) },
							onAbsenceCheck = { periodData ->
								absenceCheckViewModel.show(periodItem.originalPeriod, periodData, uiState.studentData)
							}
						)
					}
				}

				if (periodItems.size > 1)
					HorizontalPagerIndicator(
						pagerState = pagerState,
						activeColor = MaterialTheme.colorScheme.primary,
						inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
						modifier = Modifier
							.align(Alignment.CenterHorizontally)
							.padding(16.dp)
							.bottomInsets()
					)
			}

			AbsenceCheck(
				modifier = Modifier
					.fillMaxSize()
					.background(MaterialTheme.colorScheme.surface),
				viewModel = absenceCheckViewModel
			)
		}
	}
}

@Composable
private fun TimetableItemDetailsDialogPage(
	periodItem: PeriodItem,
	periodData: PeriodData?,
	error: Throwable? = null,
	onElementClick: (element: ElementEntity) -> Unit,
	onAbsenceCheck: (periodData: PeriodData) -> Unit,
	viewModel: TimetableItemDetailsDialogViewModel = hiltViewModel()
) {
	val uriHandler = LocalUriHandler.current
	val title = periodItem.subjects.toLongString().let { title ->
		if (periodItem.isCancelled())
			stringResource(R.string.all_lesson_cancelled, title)
		else if (periodItem.isIrregular())
			stringResource(R.string.all_lesson_irregular, title)
		else if (periodItem.isExam())
			stringResource(R.string.all_lesson_exam, title)
		else
			title
	}

	val errorMessage = error?.message?.let { stringResource(id = R.string.all_error_details, it) }

	val time = stringResource(
		R.string.main_dialog_itemdetails_timeformat,
		periodItem.originalPeriod.startDateTime.format(
			DateTimeFormatter.ofLocalizedTime(
				FormatStyle.SHORT
			)
		),
		periodItem.originalPeriod.endDateTime.format(
			DateTimeFormatter.ofLocalizedTime(
				FormatStyle.SHORT
			)
		)
	)

	var errorDialog by rememberSaveable { mutableStateOf<String?>(null) }

	var attachmentsDialog by rememberSaveable {
		mutableStateOf<List<Attachment>?>(
			null
		)
	}

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.bottomInsets()
	) {
		Icon(
			painter = painterResource(R.drawable.all_subject),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.tertiary,
			modifier = Modifier
				.padding(top = 24.dp, bottom = 8.dp)
				.size(dimensionResource(id = R.dimen.size_header_icon))
		)

		Text(
			text = title,
			style = MaterialTheme.typography.headlineSmall,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(horizontal = 8.dp)
		)

		Text(
			text = time,
			style = MaterialTheme.typography.labelLarge,
			modifier = Modifier.padding(top = 8.dp)
		)

		HorizontalDivider(
			color = MaterialTheme.colorScheme.outline,
			modifier = Modifier
				.padding(top = 24.dp, bottom = 12.dp)
				.padding(horizontal = 16.dp)
		)

		// Lesson teachers
		TimetableItemDetailsDialogElement(
			elements = periodItem.teachers,
			onElementClick = { onElementClick(it) },
			useLongName = true,
			icon = {
				Icon(
					painter = painterResource(id = R.drawable.all_teachers),
					contentDescription = stringResource(id = R.string.all_teachers),
					tint = MaterialTheme.colorScheme.onSurface,
					modifier = Modifier.padding(start = 8.dp)
				)
			}
		)

		// Lesson classes
		TimetableItemDetailsDialogElement(
			elements = periodItem.classes,
			onElementClick = { onElementClick(it) },
			icon = {
				Icon(
					painter = painterResource(id = R.drawable.all_classes),
					contentDescription = stringResource(id = R.string.all_classes),
					tint = MaterialTheme.colorScheme.onSurface,
					modifier = Modifier.padding(start = 8.dp)
				)
			}
		)

		// Lesson rooms
		TimetableItemDetailsDialogElement(
			elements = periodItem.rooms,
			onElementClick = { onElementClick(it) },
			icon = {
				Icon(
					painter = painterResource(id = R.drawable.all_rooms),
					contentDescription = stringResource(id = R.string.all_rooms),
					tint = MaterialTheme.colorScheme.onSurface,
					modifier = Modifier.padding(start = 8.dp)
				)
			}
		)

		// Lesson info texts
		setOf(
			periodItem.originalPeriod.text.lesson,
			periodItem.originalPeriod.text.substitution,
			periodItem.originalPeriod.text.info
		).forEach {
			if (it.isNotBlank())
				SelectionContainer {
					ListItem(
						headlineContent = { Text(it) },
						leadingContent = {
							Icon(
								painter = painterResource(id = R.drawable.all_info),
								contentDescription = stringResource(id = R.string.all_lesson_info),
								tint = MaterialTheme.colorScheme.onSurface,
								modifier = Modifier.padding(horizontal = 8.dp)
							)
						}
					)
				}
		}

		// Lesson homeworks
		periodItem.originalPeriod.homeWorks?.forEach { homeWork ->
			val endDate = homeWork.endDate

			ListItem(
				headlineContent = {
					ClickableUrlText(homeWork.text) {
						uriHandler.openUri(it)
					}
				},
				supportingContent = {
					Text(
						stringResource(
							id = R.string.homeworks_due_time,
							endDate.format(
								DateTimeFormatter.ofPattern(
									stringResource(R.string.homeworks_due_time_format)
								)
							)
						)
					)
				},
				leadingContent = {
					Icon(
						painter = painterResource(id = R.drawable.all_homework),
						contentDescription = stringResource(id = R.string.all_homework),
						tint = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier.padding(horizontal = 8.dp)
					)
				},
				trailingContent = if (homeWork.attachments.isNotEmpty()) {
					{
						IconButton(onClick = {
							attachmentsDialog = homeWork.attachments
						}) {
							Icon(
								painter = painterResource(id = R.drawable.infocenter_attachments),
								contentDescription = stringResource(id = R.string.infocenter_messages_attachments)
							)
						}
					}
				} else null
			)
		}

		// Lesson exam
		periodItem.originalPeriod.exam?.also { exam ->
			ListItem(
				headlineContent = {
					Text(exam.name ?: stringResource(id = R.string.all_exam))
				},
				supportingContent = exam.text?.let { { Text(it) } },
				leadingContent = {
					Icon(
						painter = painterResource(id = R.drawable.infocenter_exam),
						contentDescription = stringResource(id = R.string.all_exam),
						tint = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier.padding(horizontal = 8.dp)
					)
				}
			)
		}

		// Online lesson
		if (periodItem.originalPeriod.isOnlinePeriod == true) {
			ListItem(
				headlineContent = { Text(stringResource(R.string.all_lesson_online)) },
				leadingContent = {
					Icon(
						painter = painterResource(id = R.drawable.all_lesson_online),
						contentDescription = stringResource(id = R.string.all_lesson_info),
						tint = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier.padding(horizontal = 8.dp)
					)
				},
				trailingContent = periodItem.originalPeriod.onlinePeriodLink?.let {
					{
						IconButton(onClick = {
							uriHandler.openUri(it)
						}) {
							Icon(
								painter = painterResource(id = R.drawable.all_open_in_new),
								contentDescription = stringResource(R.string.all_open_link)
							)
						}
					}
				}
			)
		}

		// Lesson absence check
		if (periodItem.originalPeriod.can(PeriodRight.READ_STUD_ABSENCE))
			ListItemWithPeriodData(
				periodData = periodData,
				error = error,
				headlineContent = {
					Text(stringResource(id = R.string.all_absences))
				},
				supportingContent = {
					Text(
						stringResource(
							if (it.absenceChecked) R.string.all_dialog_absences_checked
							else R.string.all_dialog_absences_not_checked
						)
					)
				},
				leadingContent = {
					Icon(
						painter = painterResource(
							if (it?.absenceChecked == true)
								R.drawable.all_absences_checked
							else
								R.drawable.all_absences
						),
						contentDescription = stringResource(id = R.string.all_absences),
						tint = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier.padding(horizontal = 8.dp)
					)
				},
				onClick = {
					errorMessage?.let {
						errorDialog = it
					} ?: periodData?.let {
						if (periodItem.originalPeriod.can(PeriodRight.WRITE_STUD_ABSENCE)) {
							onAbsenceCheck(it)
						}
					}
				}
			)

		// Lesson topic
		val uiState by viewModel.uiState.collectAsStateWithLifecycle()
		val lessonTopicUiState by viewModel.lessonTopicUiState.collectAsStateWithLifecycle()
		val visibleLessonTopic = uiState.newLessonTopics[periodData?.ttId] ?: periodData?.topic?.text ?: ""

		if (periodItem.originalPeriod.can(PeriodRight.READ_LESSONTOPIC))
			ListItemWithPeriodData(
				periodData = periodData,
				error = error,
				headlineContent = {
					Text(stringResource(id = R.string.all_lessontopic))
				},
				supportingContent = {
					Text(
						visibleLessonTopic.ifBlank {
							if (periodItem.originalPeriod.can(PeriodRight.WRITE_LESSONTOPIC))
								stringResource(R.string.all_hint_tap_to_edit)
							else
								stringResource(R.string.all_lessontopic_none)
						}
					)
				},
				leadingContent = {
					Icon(
						painter = painterResource(id = R.drawable.all_lessontopic),
						contentDescription = stringResource(id = R.string.all_lessontopic),
						tint = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier.padding(horizontal = 8.dp)
					)
				},
				onClick = {
					errorMessage?.let {
						errorDialog = it
					} ?: run {
						if (periodItem.originalPeriod.can(PeriodRight.WRITE_LESSONTOPIC)) {
							viewModel.showLessonTopic(periodItem.originalPeriod.id, visibleLessonTopic)
						}
					}
				}
			)

		errorDialog?.let { error ->
			AlertDialog(
				onDismissRequest = { errorDialog = null },
				title = { Text(stringResource(id = R.string.all_error)) },
				text = { Text(error) },
				confirmButton = {
					TextButton(
						onClick = { errorDialog = null }
					) {
						Text(stringResource(id = R.string.all_ok))
					}
				}
			)
		}

		attachmentsDialog?.let { attachments ->
			AttachmentsDialog(
				attachments = attachments,
				onDismiss = { attachmentsDialog = null }
			)
		}

		lessonTopicUiState.periodId?.let { periodId ->
			LessonTopicDialog(
				uiState = lessonTopicUiState,
				text = lessonTopicUiState.lessonTopic,
				onTextChanged = { viewModel.setLessonTopic(it) },
				onConfirm = {
					viewModel.submitLessonTopic(periodId, lessonTopicUiState.lessonTopic)
				},
				onDismiss = {
					viewModel.resetLessonTopicState()
				}
			)
		}
	}
}

@Composable
fun LessonTopicDialog(
	text: String,
	uiState: LessonTopicUiState,
	onTextChanged: (String) -> Unit,
	onConfirm: (String) -> Unit,
	onDismiss: () -> Unit
) {
	DynamicHeightAlertDialog(
		title = { Text(stringResource(id = R.string.all_lessontopic_edit)) },
		text = {
			Column(
				modifier = Modifier.fillMaxWidth()
			) {
				OutlinedTextField(
					value = text,
					onValueChange = onTextChanged,
					isError = uiState.error != null,
					enabled = !uiState.loading,
					label = { Text(stringResource(id = R.string.all_lessontopic)) },
					modifier = Modifier.fillMaxWidth()
				)

				AnimatedVisibility(visible = uiState.error != null) {
					Text(
						modifier = Modifier.padding(
							horizontal = 16.dp,
							vertical = 4.dp
						),
						color = MaterialTheme.colorScheme.error,
						style = MaterialTheme.typography.bodyMedium,
						text = uiState.error?.stringResource() ?: ""
					)
				}
			}
		},
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(
				enabled = !uiState.loading,
				onClick = {
					onConfirm(text)
				}) {
				Text(stringResource(id = R.string.all_ok))
			}
		},
		dismissButton = {
			TextButton(
				enabled = !uiState.loading,
				onClick = onDismiss
			) {
				Text(stringResource(id = R.string.all_cancel))
			}
		}
	)
}

@Composable
private fun ListItemWithPeriodData(
	periodData: PeriodData?,
	error: Throwable?,
	headlineContent: @Composable () -> Unit,
	supportingContent: @Composable (PeriodData) -> Unit,
	leadingContent: @Composable (PeriodData?) -> Unit,
	onClick: () -> Unit
) {
	ListItem(
		headlineContent = headlineContent,
		supportingContent = {
			periodData?.let {
				supportingContent(it)
			} ?: error?.let {
				Text(stringResource(R.string.all_error))
			} ?: Text(stringResource(R.string.loading))
		},
		leadingContent = { leadingContent(periodData) },
		modifier = Modifier
			.clickable {
				onClick()
			}
	)
}

@Composable
private fun TimetableItemDetailsDialogElement(
	elements: List<PeriodElementEntity>,
	icon: (@Composable () -> Unit)? = null,
	useLongName: Boolean = false,
	onElementClick: (element: ElementEntity) -> Unit
) {
	if (elements.isNotEmpty())
		ListItem(
			headlineContent = {
				Row(
					modifier = Modifier.horizontalScroll(rememberScrollState()),
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					elements.flatMap { e -> listOfNotNull(e.entity to false, e.replacementEntity?.let { it to true }) }
						.forEach { (element, isReplacement) ->
							Text(
								text = if (useLongName) element.getLongName() else element.getShortName(),
								style = LocalTextStyle.current.let {
									if (isReplacement) it.copy(textDecoration = TextDecoration.LineThrough) else it
								},
								modifier = Modifier
									.clip(RoundedCornerShape(50))
									.clickable {
										onElementClick(element)
									}
									.padding(8.dp)
							)
						}
				}
			},
			leadingContent = icon
		)
}
