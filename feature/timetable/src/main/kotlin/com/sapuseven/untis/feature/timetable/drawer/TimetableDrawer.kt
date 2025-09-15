package com.sapuseven.untis.feature.timetable.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.model.ElementType
import com.sapuseven.untis.feature.timetable.R
import kotlinx.coroutines.launch

@Composable
fun TimetableDrawer(
	viewModel: TimetableDrawerViewModel = hiltViewModel(),
	drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
	displayedElement: ElementEntity? = null,
	onElementPicked: (ElementEntity?) -> Unit,
	content: @Composable () -> Unit
) {
	val scope = rememberCoroutineScope()
	val drawerScrollState = rememberScrollState()

	var showElementPicker by remember {
		mutableStateOf<ElementType?>(
			null
		)
	}

	var bookmarksElementPicker by remember {
		mutableStateOf<ElementType?>(
			null
		)
	}

	val elements by viewModel.elements.collectAsStateWithLifecycle()

	/*val shortcutLauncher =
		rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
			val periodElement: PeriodElement? = activityResult.data?.let { intent ->
				Json.decodeFromString(
					PeriodElement.serializer(),
					intent.getStringExtra(MainActivity.EXTRA_STRING_PERIOD_ELEMENT) ?: ""
				)
			}

			periodElement?.let {
				//onShowTimetable(it to state.timetableDatabaseInterface.getLongName(it))
			}
		}

	LaunchedEffect(state.drawerState) {
		snapshotFlow { state.drawerState.isOpen }
			.distinctUntilChanged()
			.drop(1)
			.collect {
				Log.i("Sentry", "Drawer isOpen: ${state.drawerState.isOpen}")
				Breadcrumb().apply {
					category = "ui.drawer"
					level = SentryLevel.INFO
					setData("isOpen", state.drawerState.isOpen)
					Sentry.addBreadcrumb(this)
				}
			}
	}*/

	BackHandler(enabled = drawerState.isOpen) {
		scope.launch {
			drawerState.close()
		}
	}

	ModalNavigationDrawer(
		gesturesEnabled = viewModel.enableDrawerGestures,
		drawerState = drawerState,
		drawerContent = {
			ModalDrawerSheet(
				modifier = Modifier
					.width(320.dp) // default: 360.dp
					.fillMaxHeight()
					.verticalScroll(drawerScrollState)
			) {
				Spacer(modifier = Modifier.height(24.dp))

				DrawerText(stringResource(id = R.string.feature_timetable_favourites))

				NavigationDrawerItem(
					icon = {
						Icon(
							painterResource(id = com.sapuseven.untis.core.ui.R.drawable.core_ui_personal),
							contentDescription = null
						)
					},
					label = { Text(stringResource(id = R.string.feature_timetable_personal_timetable)) },
					selected = displayedElement == null,
					onClick = {
						scope.launch {
							drawerState.close()
							onElementPicked(null)
						}
					},
					modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
				)

				val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
				val isBookmarkSelected = bookmarks.any { bookmark ->
					displayedElement?.equals(bookmark) == true
				}
				bookmarks.forEach { bookmark ->
					NavigationDrawerItem(
						icon = {
							Icon(
								painter = painterResource(
									id = when (bookmark.type) {
										ElementType.CLASS -> com.sapuseven.untis.core.ui.R.drawable.core_ui_classes
										ElementType.TEACHER -> com.sapuseven.untis.core.ui.R.drawable.core_ui_teachers
										ElementType.SUBJECT -> com.sapuseven.untis.core.ui.R.drawable.core_ui_subject
										ElementType.ROOM -> com.sapuseven.untis.core.ui.R.drawable.core_ui_rooms
										else -> com.sapuseven.untis.core.ui.R.drawable.core_ui_personal
									}
								),
								contentDescription = null
							)
						},
						badge = {
							IconButton(
								onClick = { viewModel.showBookmarkDeleteDialog(bookmark) }
							) {
								Icon(
									painter = painterResource(id = R.drawable.feature_timetable_bookmark_remove),
									contentDescription = "Remove Bookmark"
								) //TODO: Extract String resource
							}
						},
						label = { Text(text = viewModel.getBookmarkDisplayName(bookmark)) },
						selected = displayedElement?.equals(bookmark) == true,
						onClick = {
							scope.launch {
								scope.launch { drawerState.close() }
								onElementPicked(bookmark)
							}
						},
						modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
					)
				}

				NavigationDrawerItem(
					icon = {
						Icon(
							painterResource(id = R.drawable.feature_timetable_add),
							contentDescription = null
						)
					},
					label = { Text(stringResource(id = R.string.feature_timetable_add_bookmark)) },
					selected = false,
					onClick = {
						bookmarksElementPicker = ElementType.CLASS
					},
					modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
				)

				DrawerText(stringResource(id = R.string.feature_timetable_navigation_timetables))

				DrawerItems(
					disableTypeSelection = displayedElement == null || isBookmarkSelected,
					displayedElement = displayedElement,
					/*onTimetableClick = { item ->
						scope.launch { drawerState.close() }
						showElementPicker = item.elementType
					},
					onNavigationClick = { item ->
						scope.launch { drawerState.close() }
						onItemPicked(item)
					}*/
				)
			}
		},
		content = content
	)

	/*AnimatedVisibility(
		visible = showElementPicker != null,
		//enter = fullscreenDialogAnimationEnter(),
		//exit = fullscreenDialogAnimationExit()
	) {
		ElementPickerDialogFullscreen(
			title = { /*TODO*/ },
			elements = elements,
			onDismiss = { showElementPicker = null },
			onSelect = { item ->
				item?.let {
					onElementPicked(item)
				} ?: run {
					onElementPicked(null)
				}
			},
			initialType = showElementPicker
		)
	}

	AnimatedVisibility(
		visible = bookmarksElementPicker != null,
		//enter = fullscreenDialogAnimationEnter(),
		//exit = fullscreenDialogAnimationExit()
	) {
		ElementPickerDialogFullscreen(
			title = { /*TODO*/ },
			elements = elements,
			hideTypeSelectionPersonal = true,
			onDismiss = { bookmarksElementPicker = null },
			onSelect = { item ->
				item?.let {
					viewModel.onBookmarkAdd(it)
				}
			},
			initialType = bookmarksElementPicker
		)
	}*/

	viewModel.bookmarkDeleteDialog?.let { bookmark ->
		AlertDialog(
			text = { Text(stringResource(id = R.string.feature_timetable_delete_bookmark_dialog_message)) },
			onDismissRequest = { viewModel.dismissBookmarkDeleteDialog() },
			confirmButton = {
				TextButton(
					onClick = {
						viewModel.onBookmarkRemove(bookmark)
					}) {
					Text(stringResource(id = R.string.feature_timetable_delete))
				}
			},
			dismissButton = {
				TextButton(
					onClick = { viewModel.dismissBookmarkDeleteDialog() }) {
					Text(stringResource(id = R.string.feature_timetable_cancel))
				}
			}
		)
	}
}

@Composable
fun DrawerItems(
	disableTypeSelection: Boolean = false,
	displayedElement: ElementEntity? = null,
	//onTimetableClick: (item: NavItemTimetable) -> Unit,
	//onNavigationClick: (item: NavItemNavigation) -> Unit,
) {
	/*val navItemsElementTypes = listOf(
		NavItemTimetable(
			icon = painterResource(id = com.sapuseven.untis.core.ui.R.drawable.core_ui_classes),
			label = stringResource(id = com.sapuseven.untis.R.string.all_classes),
			elementType = ElementType.CLASS
		),
		NavItemTimetable(
			icon = painterResource(id = com.sapuseven.untis.core.ui.R.drawable.core_ui_teachers),
			label = stringResource(id = com.sapuseven.untis.R.string.all_teachers),
			elementType = ElementType.TEACHER
		),
		NavItemTimetable(
			icon = painterResource(id = com.sapuseven.untis.core.ui.R.drawable.core_ui_rooms),
			label = stringResource(id = com.sapuseven.untis.R.string.all_rooms),
			elementType = ElementType.ROOM
		),
	)

	/*val navItemsNavigation = listOf(
		NavItemNavigation(
			icon = painterResource(id = R.drawable.all_infocenter),
			label = stringResource(id = R.string.activity_title_info_center),
			route = AppRoutes.InfoCenter
		),
		NavItemNavigation(
			icon = painterResource(id = R.drawable.all_search_rooms),
			label = stringResource(id = R.string.activity_title_free_rooms),
			route = AppRoutes.RoomFinder
		),
		NavItemNavigation(
			icon = painterResource(id = R.drawable.all_settings),
			label = stringResource(id = R.string.activity_title_settings),
			route = AppRoutes.Settings
		)
	)*/

	navItemsElementTypes.forEach { item ->
		NavigationDrawerItem(
			icon = { Icon(item.icon, contentDescription = null) },
			label = { Text(item.label) },
			selected = !disableTypeSelection && item.elementType == displayedElement?.type,
			onClick = { onTimetableClick(item) },
			modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
		)
	}

	DrawerDivider()

	/*navItemsNavigation.forEach { item ->
		NavigationDrawerItem(
			icon = { Icon(item.icon, contentDescription = null) },
			label = { Text(item.label) },
			selected = false,
			onClick = { onNavigationClick(item) },
			modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
		)
	}*/*/
}

@Composable
fun DrawerDivider() {
	HorizontalDivider(
		color = MaterialTheme.colorScheme.outline,
		modifier = Modifier.padding(vertical = 8.dp)
	)
}

@Composable
fun DrawerText(text: String) {
	Text(
		text = text,
		style = MaterialTheme.typography.labelMedium,
		modifier = Modifier.padding(start = 28.dp, top = 16.dp, bottom = 8.dp)
	)
}

