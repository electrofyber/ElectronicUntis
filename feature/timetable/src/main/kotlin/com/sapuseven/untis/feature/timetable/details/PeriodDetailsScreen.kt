package com.sapuseven.untis.feature.timetable.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sapuseven.untis.core.domain.timetable.toLongString
import com.sapuseven.untis.core.model.timetable.Attachment
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.model.timetable.Period
import com.sapuseven.untis.core.ui.common.ClickableUrlText
import com.sapuseven.untis.core.ui.common.HorizontalPagerIndicator
import com.sapuseven.untis.core.ui.functional.None
import com.sapuseven.untis.core.ui.functional.bottomInsets
import com.sapuseven.untis.feature.timetable.R
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PeriodDetailsScreen(
	onBackClick: () -> Unit,
	onElementClick: (id: Long?, type: ElementType?) -> Unit,
	viewModel: PeriodDetailsViewModel = hiltViewModel(),
	//periodItems: List<PeriodItem>,
	//onDismiss: (requestedElement: ElementEntity?) -> Unit,
	//absenceCheckViewModel: AbsenceCheckViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val pagerState = rememberPagerState(uiState.initialPeriod) { uiState.periods.size }

	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(
						/*absenceCheckUiState.detailedPerson?.fullName()
							?:*/ stringResource(id = R.string.feature_timetable_lesson_details)
					)
				},
				navigationIcon = {
					IconButton(onClick = {
						/*if (absenceCheckUiState.visible)
							absenceCheckViewModel.hide()
						else*/
						onBackClick()
					}) {
						Icon(
							imageVector = Icons.Outlined.Close,
							contentDescription = stringResource(id = com.sapuseven.untis.core.ui.R.string.core_ui_action_close)
						)
					}
				},
				/*actions = {
					if (BuildConfig.DEBUG)
						DebugTimetableItemDetailsAction(periodItems, uiState.periodDataMap)
				}*/
			)
		},
		contentWindowInsets = WindowInsets.None
	) { paddingValues ->
		Surface(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues),
			color = MaterialTheme.colorScheme.background,
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
					uiState.periods[page]?.also { period ->
						TimetableItemDetailsDialogPage(
							period = period,
							periodData = null,//uiState.periodDataMap[periodItem.id],
							//error = error,
							//viewModel = viewModel,
							onElementClick = { onElementClick(it.id, it.type) },
							/*onAbsenceCheck = { periodData ->
								absenceCheckViewModel.show(periodItem, periodData, uiState.studentData)
							}*/
						)
					}
				}

				if (uiState.periods.size > 1)
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
		}
	}
}


@Composable
private fun TimetableItemDetailsDialogPage(
	period: Period,
	periodData: Nothing?,//PeriodData?,
	error: Throwable? = null,
	onElementClick: (element: Element) -> Unit,
	//onAbsenceCheck: (periodData: PeriodData) -> Unit,
	//viewModel: TimetableItemDetailsDialogViewModel = hiltViewModel()
) {
	val uriHandler = LocalUriHandler.current
	val title = period.subjects.toLongString().let { title ->
		if (period.isCancelled())
			stringResource(R.string.feature_timetable_lesson_cancelled, title)
		else if (period.isIrregular())
			stringResource(R.string.feature_timetable_lesson_irregular, title)
		else if (period.isExam())
			stringResource(R.string.feature_timetable_lesson_exam, title)
		else
			title
	}

	val errorMessage = error?.message?.let { stringResource(id = R.string.feature_timetable_error_details, it) }

	val time = stringResource(
		R.string.feature_timetable_main_dialog_itemdetails_timeformat,
		period.startDateTime.toJavaLocalDateTime().format(
			DateTimeFormatter.ofLocalizedTime(
				FormatStyle.SHORT
			)
		),
		period.endDateTime.toJavaLocalDateTime().format(
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
			painter = painterResource(com.sapuseven.untis.core.ui.R.drawable.core_ui_subject),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.tertiary,
			modifier = Modifier
				.padding(top = 24.dp, bottom = 8.dp)
				.size(48.dp)
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
			elements = period.teachers,
			onElementClick = { onElementClick(it) },
			useLongName = true,
			icon = {
				Icon(
					painter = painterResource(id = com.sapuseven.untis.core.ui.R.drawable.core_ui_teachers),
					contentDescription = stringResource(id = R.string.feature_timetable_teachers),
					tint = MaterialTheme.colorScheme.onSurface,
					modifier = Modifier.padding(start = 8.dp)
				)
			}
		)

		// Lesson classes
		TimetableItemDetailsDialogElement(
			elements = period.classes,
			onElementClick = { onElementClick(it) },
			icon = {
				Icon(
					painter = painterResource(id = com.sapuseven.untis.core.ui.R.drawable.core_ui_classes),
					contentDescription = stringResource(id = R.string.feature_timetable_classes),
					tint = MaterialTheme.colorScheme.onSurface,
					modifier = Modifier.padding(start = 8.dp)
				)
			}
		)

		// Lesson rooms
		TimetableItemDetailsDialogElement(
			elements = period.rooms,
			onElementClick = { onElementClick(it) },
			icon = {
				Icon(
					painter = painterResource(id = com.sapuseven.untis.core.ui.R.drawable.core_ui_rooms),
					contentDescription = stringResource(id = R.string.feature_timetable_rooms),
					tint = MaterialTheme.colorScheme.onSurface,
					modifier = Modifier.padding(start = 8.dp)
				)
			}
		)

		// Lesson info texts
		period.infoTexts.forEach {
			SelectionContainer {
				ListItem(
					headlineContent = { Text(it.text) },
					leadingContent = {
						Icon(
							painter = painterResource(id = R.drawable.feature_timetable_info),
							contentDescription = stringResource(id = R.string.feature_timetable_lesson_info),
							tint = MaterialTheme.colorScheme.onSurface,
							modifier = Modifier.padding(horizontal = 8.dp)
						)
					}
				)
			}
		}

		// Lesson homeworks
		period.homeworks.forEach { homeWork ->
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
							id = R.string.feature_timetable_homeworks_due_time,
							endDate.toJavaLocalDate().format(
								DateTimeFormatter.ofPattern(
									stringResource(R.string.feature_timetable_homeworks_due_time_format)
								)
							)
						)
					)
				},
				leadingContent = {
					Icon(
						painter = painterResource(id = R.drawable.infocenter_homework),
						contentDescription = stringResource(id = R.string.feature_timetable_homework),
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
								contentDescription = stringResource(id = R.string.feature_timetable_infocenter_messages_attachments)
							)
						}
					}
				} else null
			)
		}

		// Lesson exam
		period.exam?.also { exam ->
			ListItem(
				headlineContent = {
					Text(exam.name ?: stringResource(id = R.string.feature_timetable_exam))
				},
				supportingContent = exam.text?.let { { Text(it) } },
				leadingContent = {
					Icon(
						painter = painterResource(id = R.drawable.infocenter_exam),
						contentDescription = stringResource(id = R.string.feature_timetable_exam),
						tint = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier.padding(horizontal = 8.dp)
					)
				}
			)
		}

		// Online lesson
		if (period.onlinePeriod == true) {
			ListItem(
				headlineContent = { Text(stringResource(R.string.feature_timetable_lesson_online)) },
				leadingContent = {
					Icon(
						painter = painterResource(id = R.drawable.feature_timetable_lesson_online),
						contentDescription = stringResource(id = R.string.feature_timetable_lesson_info),
						tint = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier.padding(horizontal = 8.dp)
					)
				},
				trailingContent = period.onlinePeriodLink?.let {
					{
						IconButton(onClick = {
							uriHandler.openUri(it)
						}) {
							Icon(
								painter = painterResource(id = R.drawable.feature_timetable_open_in_new),
								contentDescription = stringResource(R.string.feature_timetable_open_link)
							)
						}
					}
				}
			)
		}

		// Lesson absence check
		/*if (period.rights.contains(PeriodRight.READ_STUD_ABSENCE))
			ListItemWithPeriodData(
				periodData = periodData,
				error = error,
				headlineContent = {
					Text(stringResource(id = R.string.feature_timetable_absences))
				},
				supportingContent = {
					Text(
						stringResource(
							if (it.absenceChecked) R.string.feature_timetable_dialog_absences_checked
							else R.string.feature_timetable_dialog_absences_not_checked
						)
					)
				},
				leadingContent = {
					Icon(
						painter = painterResource(
							if (it?.absenceChecked == true)
								R.drawable.feature_timetable_absences_checked
							else
								R.drawable.feature_timetable_absences
						),
						contentDescription = stringResource(id = R.string.feature_timetable_absences),
						tint = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier.padding(horizontal = 8.dp)
					)
				},
				onClick = {
					errorMessage?.let {
						errorDialog = it
					} ?: periodData?.let {
						if (period.can(PeriodRight.WRITE_STUD_ABSENCE)) {
							onAbsenceCheck(it)
						}
					}
				}
			)

		// Lesson topic
		val uiState by viewModel.uiState.collectAsStateWithLifecycle()
		val lessonTopicUiState by viewModel.lessonTopicUiState.collectAsStateWithLifecycle()
		val visibleLessonTopic = uiState.newLessonTopics[periodData?.ttId] ?: periodData?.topic?.text ?: ""

		if (period.can(PeriodRight.READ_LESSONTOPIC))
			ListItemWithPeriodData(
				periodData = periodData,
				error = error,
				headlineContent = {
					Text(stringResource(id = R.string.feature_timetable_lessontopic))
				},
				supportingContent = {
					Text(
						visibleLessonTopic.ifBlank {
							if (period.can(PeriodRight.WRITE_LESSONTOPIC))
								stringResource(R.string.feature_timetable_hint_tap_to_edit)
							else
								stringResource(R.string.feature_timetable_lessontopic_none)
						}
					)
				},
				leadingContent = {
					Icon(
						painter = painterResource(id = R.drawable.feature_timetable_lessontopic),
						contentDescription = stringResource(id = R.string.feature_timetable_lessontopic),
						tint = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier.padding(horizontal = 8.dp)
					)
				},
				onClick = {
					errorMessage?.let {
						errorDialog = it
					} ?: run {
						if (period.can(PeriodRight.WRITE_LESSONTOPIC)) {
							viewModel.showLessonTopic(period.id, visibleLessonTopic)
						}
					}
				}
			)

		errorDialog?.let { error ->
			AlertDialog(
				onDismissRequest = { errorDialog = null },
				title = { Text(stringResource(id = R.string.feature_timetable_error)) },
				text = { Text(error) },
				confirmButton = {
					TextButton(
						onClick = { errorDialog = null }
					) {
						Text(stringResource(id = R.string.feature_timetable_ok))
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
		}*/
	}
}

@Composable
private fun TimetableItemDetailsDialogElement(
	elements: List<Element>,
	icon: (@Composable () -> Unit)? = null,
	useLongName: Boolean = false,
	onElementClick: (element: Element) -> Unit
) {
	if (elements.isNotEmpty())
		ListItem(
			headlineContent = {
				Row(
					modifier = Modifier.horizontalScroll(rememberScrollState()),
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					elements.forEach { element ->
						Text(
							text = if (useLongName) element.longName else element.shortName,
							style = LocalTextStyle.current.let {
								if (element.replaced) it.copy(textDecoration = TextDecoration.LineThrough) else it
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

/*	var dismissed by rememberSaveable { mutableStateOf(false) }
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
		val periods = periodItems.map { it }.toSet()
		Log.d("TimetableItemDetailsDlg", "Fetching period data for ${periods.map { it.id }}")
		viewModel.loadPeriodData(periods)
	}

	LaunchedEffect(absenceCheckUiState.periodData) {
		absenceCheckUiState.periodData?.let { updatedPeriodData ->
			viewModel.updatePeriodData(updatedPeriodData)
		}
	}

	AppScaffold(
		floatingActionButton = {
			AnimatedVisibility(
				visible = absenceCheckUiState.visible,
				enter = scaleIn(),
				exit = scaleOut()
			) {
				FloatingActionButton(
					modifier = Modifier.Companion.bottomInsets(),
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
							painter = painterResource(id = R.drawable.feature_timetable_check),
							contentDescription = stringResource(R.string.feature_timetable_dialog_absences_save_detailed)
						)
					else
						Icon(
							painter = painterResource(id = R.drawable.feature_timetable_save),
							contentDescription = stringResource(R.string.feature_timetable_dialog_absences_save)
						)
				}
			}
		}
	) { innerPadding ->
		Box(
			modifier = padding(innerPadding)
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
							periodData = uiState.periodDataMap[periodItem.id],
							error = error,
							viewModel = viewModel,
							onElementClick = { dismiss(it) },
							onAbsenceCheck = { periodData ->
								absenceCheckViewModel.show(periodItem, periodData, uiState.studentData)
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
fun LessonTopicDialog(
	text: String,
	uiState: LessonTopicUiState,
	onTextChanged: (String) -> Unit,
	onConfirm: (String) -> Unit,
	onDismiss: () -> Unit
) {
	DynamicHeightAlertDialog(
		title = { Text(stringResource(id = R.string.feature_timetable_lessontopic_edit)) },
		text = {
			Column(
				modifier = Modifier.fillMaxWidth()
			) {
				OutlinedTextField(
					value = text,
					onValueChange = onTextChanged,
					isError = uiState.error != null,
					enabled = !uiState.loading,
					label = { Text(stringResource(id = R.string.feature_timetable_lessontopic)) },
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
				Text(stringResource(id = R.string.feature_timetable_ok))
			}
		},
		dismissButton = {
			TextButton(
				enabled = !uiState.loading,
				onClick = onDismiss
			) {
				Text(stringResource(id = R.string.feature_timetable_cancel))
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
				Text(stringResource(R.string.feature_timetable_error))
			} ?: Text(stringResource(R.string.loading))
		},
		leadingContent = { leadingContent(periodData) },
		modifier = Modifier
			.clickable {
				onClick()
			}
	)
}
*/
