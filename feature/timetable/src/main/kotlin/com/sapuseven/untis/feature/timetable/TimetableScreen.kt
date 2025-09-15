package com.sapuseven.untis.feature.timetable

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sapuseven.untis.core.model.ElementType
import com.sapuseven.untis.core.ui.common.ProfileSelectorAction
import com.sapuseven.untis.core.ui.functional.insetsPaddingValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimetableScreen(
	onElementClicked: (id: Long?, type: ElementType?) -> Unit,
	onUserEdit: (userId: Long?) -> Unit,
	viewModel: TimetableViewModel = hiltViewModel()
	//factory: TimetableViewModel.Factory = hiltEntryPointViewModelFactory() // explained below
	/*viewModel: TimetableViewModel = hiltViewModel<TimetableViewModel, TimetableViewModel.Factory>(
		creationCallback = { factory -> factory.create(colorScheme, typography) }
	)*/
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	/*val ready by viewModel.ready.collectAsStateWithLifecycle()

	val scope = rememberCoroutineScope()
	val user = viewModel.currentUser
	val users by viewModel.allUsersState.collectAsStateWithLifecycle()

	val needsPersonalTimetable by viewModel.needsPersonalTimetable.collectAsStateWithLifecycle()
	val hourList by viewModel.hourList.collectAsStateWithLifecycle()
	val events by viewModel.events.collectAsStateWithLifecycle()
	val holidays by viewModel.holidays.collectAsStateWithLifecycle()
	val lastRefresh by viewModel.lastRefresh.collectAsStateWithLifecycle()
	val weekViewColorScheme by viewModel.weekViewColorScheme.collectAsStateWithLifecycle()
	val weekViewScale by viewModel.weekViewScale.collectAsStateWithLifecycle()
	val weekViewZoomEnabled by viewModel.weekViewZoomEnabled.collectAsStateWithLifecycle()
	val weekViewEventStyle by viewModel.weekViewEventStyle.collectAsStateWithLifecycle()*/

	DisposableEffect(LocalLifecycleOwner.current) {
		Log.d("Timetable", "Creating TimetableViewModel")

		onDispose {
			Log.d("Timetable", "Disposing TimetableViewModel")
		}
	}

	/*TimetableDrawer(
		drawerState = drawerState,
		displayedElement = viewModel.requestedElement,
		onElementPicked = {
			viewModel.showElement(it)
		}
	) {*/
		Scaffold(
			topBar = {
				CenterAlignedTopAppBar(
					title = {
						Text(uiState.title)
					},
					navigationIcon = {
						IconButton(onClick = {
							//TODO viewModel.onDrawerOpenClick()
						}) {
							Icon(
								imageVector = Icons.Outlined.Menu,
								contentDescription = "TODO"// stringResource(id = R.string.main_drawer_open)
							)
						}
					},
					actions = {
						/*if (uiState.isDebug)
							DebugDisclaimerAction(
								viewModel.debugInfoRepository.getColorSchemeDebugInfo(MaterialTheme.colorScheme.toString())
							)*/

						ProfileSelectorAction(
							users = uiState.userList,
							currentSelection = uiState.user,
							showProfileActions = true,
							onSelectionChange = {
								viewModel.switchUser(it)
							},
							onActionEdit = {
								viewModel.editUsers()
								onUserEdit(null) // TODO show management dialog
							}
						)
					}
				)
			}
		) { innerPadding ->
			/*AnimatedVisibility(ready,
				enter = fadeIn(tween()),
				exit = fadeOut(tween())
			) {*/
				Box(
					modifier = Modifier
						.padding(innerPadding)
						.fillMaxSize()
				) {
					val insets = insetsPaddingValues()
					val navBarHeight = remember { insets.calculateBottomPadding() + 48.dp }

					/*FeedbackDialog(
						visible = viewModel.feedbackDialog,
						onDismiss = { viewModel.feedbackDialog = false }
					)*/

					/*WeekViewStyle(weekViewEventStyle) {
						WeekViewCompose(
							events = events,
							holidays = holidays,
							loading = if (viewModel.loading) true else null,
							weekLogicService = viewModel.weekLogicService,
							onPageChange = { pageOffset ->
								viewModel.onPageChange(pageOffset)
							},
							onReload = { pageOffset ->
								viewModel.onPageReload(pageOffset)
							},
							onItemClick = { itemsWithIndex ->
								viewModel.onItemClick(itemsWithIndex)
							},
							onZoom = { zoomLevel ->
								viewModel.onZoom(zoomLevel)
							},
							currentTime = uiState.currentTime,
							startTime = hourList.firstOrNull()?.startTime ?: LocalTime.MIDNIGHT,
							endTime = hourList.lastOrNull()?.endTime ?: LocalTime.MIDNIGHT,
							endTimeOffset = navBarHeight,
							initialScale = weekViewScale,
							enableZoomGesture = weekViewZoomEnabled,
							hourHeight = /*state.weekViewPreferences.hourHeight ?:*/ 72.dp,
							hourList = hourList,
							//dividerWidth = viewModel.weekViewPreferences.dividerWidth,
							colorScheme = weekViewColorScheme,
							modifier = Modifier
								.fillMaxSize()
								.disabled(disabled = needsPersonalTimetable)
						) { startPadding ->
							// Feedback button
							IconButton(
								modifier = Modifier
									.align(Alignment.BottomEnd)
									.padding(end = 8.dp)
									.bottomInsets(),
								onClick = {
									viewModel.showFeedback()
								}
							) {
								Icon(
									painter = painterResource(R.drawable.all_feedback),
									contentDescription = "Give feedback"
								)
							}

							// Custom personal timetable hint
							if (needsPersonalTimetable) {
								Column(
									verticalArrangement = Arrangement.Center,
									horizontalAlignment = Alignment.CenterHorizontally,
									modifier = Modifier
										.fillMaxSize()
								) {
									Text(
										text = stringResource(id = R.string.main_anonymous_login_info_text),
										textAlign = TextAlign.Center,
										modifier = Modifier
											.padding(horizontal = 32.dp)
									)

									Button(
										onClick = viewModel.onAnonymousSettingsClick,
										modifier = Modifier
											.padding(top = 16.dp)
									) {
										Text(text = stringResource(id = R.string.main_go_to_settings))
									}
								}
							} else {
								// Last refresh text
								Text(
									text = stringResource(
										id = R.string.main_last_refreshed,
										formatTimeDiffMillis(uiState.lastRefresh?.toMillis())
									),
									modifier = Modifier
										.align(Alignment.BottomStart)
										.padding(start = startPadding + 8.dp, bottom = 8.dp)
										.bottomInsets()
								)
							}
						}
					}*/
				}
			//}
		}
	//}

	// TODO: Implement a nicer animation (see https://m3.material.io/components/dialogs/guidelines#007536b9-76b1-474a-a152-2f340caaff6f)
	/*AnimatedVisibility(
		visible = viewModel.timetableItemDetailsDialog != null,
		enter = fullscreenDialogAnimationEnter(),
		exit = fullscreenDialogAnimationExit()
	) {
		TimetableItemDetailsDialog(
			periodItems = remember {
				viewModel.timetableItemDetailsDialog?.first?.mapNotNull { it.data } ?: emptyList()
			},
			initialPage = remember {
				viewModel.timetableItemDetailsDialog?.second ?: 0
			},
			onDismiss = {
				viewModel.timetableItemDetailsDialog = null
				it?.let { viewModel.showElement(it) }
			}
		)
	}

	AnimatedVisibility(
		visible = viewModel.profileManagementDialog,
		enter = fullscreenDialogAnimationEnter(),
		exit = fullscreenDialogAnimationExit()
	) {
		ProfileManagementDialog(
			userRepository = viewModel.userRepository,
			onEdit = {
				viewModel.editUser(it)
			},
			onDismiss = {
				viewModel.profileManagementDialog = false
			}
		)
	}*/
}

@Composable
private fun formatTimeDiffMillis(diff: Long?): String {
	val MINUTE_MILLIS: Int = 60 * 1000
	val HOUR_MILLIS: Int = 60 * MINUTE_MILLIS
	val DAY_MILLIS: Int = 24 * HOUR_MILLIS

	if (diff == null) return stringResource(R.string.feature_timetable_last_refreshed_never)

	return when {
		diff < MINUTE_MILLIS -> stringResource(R.string.feature_timetable_time_diff_just_now)
		diff < HOUR_MILLIS -> pluralStringResource(
			R.plurals.feature_timetable_time_diff_minutes, ((diff / MINUTE_MILLIS).toInt()), diff / MINUTE_MILLIS
		)

		diff < DAY_MILLIS -> pluralStringResource(
			R.plurals.feature_timetable_time_diff_hours, ((diff / HOUR_MILLIS).toInt()), diff / HOUR_MILLIS
		)

		else -> pluralStringResource(
			R.plurals.feature_timetable_time_diff_days, ((diff / DAY_MILLIS).toInt()), diff / DAY_MILLIS
		)
	}
}
