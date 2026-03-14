package com.sapuseven.untis.core.ui.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.ui.R
import com.sapuseven.untis.core.ui.common.NavigationBarInset
import com.sapuseven.untis.core.ui.common.disabled
import com.sapuseven.untis.core.ui.functional.None
import com.sapuseven.untis.core.ui.functional.insetsPaddingValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElementPickerDialogFullscreen(
	title: @Composable () -> Unit,
	elements: Map<ElementType, List<Element>>,
	initialType: ElementType? = null,
	multiSelect: Boolean = false,
	hideTypeSelection: Boolean = false,
	hideTypeSelectionPersonal: Boolean = false,
	onDismiss: (success: Boolean) -> Unit = {},
	onSelect: (selectedItem: Element?) -> Unit = {},
	onMultiSelect: (selectedItems: List<Element>) -> Unit = {},
	additionalActions: (@Composable () -> Unit) = {}
) {
	val selection = remember { mutableStateSetOf<Element>() }
	var selectedType by remember { mutableStateOf(initialType) }
	var showSearch by remember { mutableStateOf(false) }
	var search by remember { mutableStateOf("") }

	BackHandler {
		onDismiss(false)
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					if (!showSearch)
						title()
					else {
						val focusRequester = remember { FocusRequester() }

						BasicTextField(
							value = search,
							onValueChange = { search = it },
							singleLine = true,
							decorationBox = { innerTextField ->
								TextFieldDefaults.DecorationBox(
									value = search,
									innerTextField = innerTextField,
									enabled = true,
									singleLine = true,
									visualTransformation = VisualTransformation.None,
									interactionSource = remember { MutableInteractionSource() },
									placeholder = { Text("Search") },
									contentPadding = PaddingValues(horizontal = 0.dp),
									colors = TextFieldDefaults.colors(
										focusedContainerColor = Color.Transparent,
										unfocusedContainerColor = Color.Transparent,
										errorContainerColor = Color.Transparent,
										disabledContainerColor = Color.Transparent,
										unfocusedIndicatorColor = Color.Transparent,
										disabledIndicatorColor = Color.Transparent
									)
								)
							},
							textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
							cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
							modifier = Modifier
								.fillMaxWidth()
								.padding(20.dp)
								.focusRequester(focusRequester)
						)

						LaunchedEffect(Unit) {
							focusRequester.requestFocus()
						}
					}
				},
				navigationIcon = {
					if (showSearch)
						IconButton(onClick = {
							showSearch = false
							search = ""
						}) {
							Icon(
								imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
								contentDescription = stringResource(id = R.string.all_back)
							)
						}
					else
						IconButton(onClick = { onDismiss(false) }) {
							Icon(
								imageVector = Icons.Outlined.Close,
								contentDescription = "Close"
							)
						}
				},
				actions = {
					if (!showSearch) {
						IconButton(onClick = { showSearch = true }) {
							Icon(
								imageVector = Icons.Outlined.Search,
								contentDescription = "Search"
							)
						}
					}

					additionalActions()

					if (multiSelect) {
						IconButton(onClick = {
							onMultiSelect(selection.toList())
							onDismiss(true)
						}) {
							Icon(Icons.Outlined.Check, "Confirm")
						}
					}
				}
			)
		},
		contentWindowInsets = WindowInsets.None
	) { innerPadding ->
		Column(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
		) {
			ElementPickerElements(
				selectedType = selectedType,
				multiSelect = multiSelect,
				onDismiss = onDismiss,
				onSelect = onSelect,
				elements = elements,
				selection = selection,
				filter = search,
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f),
				contentPadding = if (hideTypeSelection) insetsPaddingValues() else PaddingValues(0.dp)
			)

			if (!hideTypeSelection)
				ElementPickerTypeSelection(
					selectedType = selectedType,
					onTypeChange = { selectedType = it }
				)
		}
	}
}

@Composable
fun ElementPickerDialog(
	title: (@Composable () -> Unit)?,
	elements: Map<ElementType, List<Element>>,
	initialType: ElementType? = null,
	hideTypeSelection: Boolean = false,
	hideTypeSelectionPersonal: Boolean = false,
	onDismiss: (success: Boolean) -> Unit = {},
	onSelect: (selectedItem: Element?) -> Unit = {}
) {
	val selection = remember { mutableStateSetOf<Element>() }
	var selectedType by remember { mutableStateOf(initialType) }

	Dialog(onDismissRequest = { onDismiss(false) }) {
		Surface(
			modifier = Modifier.padding(vertical = 24.dp),
			shape = AlertDialogDefaults.shape,
			color = AlertDialogDefaults.containerColor,
			tonalElevation = AlertDialogDefaults.TonalElevation
		) {
			Column {
				title?.let {
					ProvideTextStyle(value = MaterialTheme.typography.headlineSmall) {
						Box(
							Modifier
								.padding(top = 24.dp, bottom = 16.dp)
								.padding(horizontal = 24.dp)
						) {
							title()
						}
					}
				}

				ElementPickerElements(
					selectedType = selectedType,
					onDismiss = onDismiss,
					onSelect = onSelect,
					elements = elements,
					selection = selection,
					modifier = Modifier
						.fillMaxWidth()
						.weight(1f)
						.padding(horizontal = 24.dp)
				)

				if (!hideTypeSelection)
					ElementPickerTypeSelection(
						selectedType = selectedType,
						onTypeChange = { selectedType = it }
					)
			}
		}
	}
}

@Composable
fun ElementPickerElements(
	selectedType: ElementType?,
	multiSelect: Boolean = false,
	modifier: Modifier,
	onDismiss: (success: Boolean) -> Unit = {},
	onSelect: (selectedItem: Element?) -> Unit = {},
	elements: Map<ElementType, List<Element>>,
	selection: MutableSet<Element>,
	filter: String = "",
	contentPadding: PaddingValues = PaddingValues(0.dp)
) {
	val visibleItems = remember(selectedType, filter, elements) {
		elements[selectedType]
			?.filter { it.shortName.contains(filter, true) }
			?.sortedWith(compareBy({ !it.timetableAllowed }, { it.shortName }))
			.orEmpty()
	}

	Box(
		contentAlignment = Alignment.Center,
		modifier = modifier
	) {
		selectedType?.let {
			LazyVerticalGrid(
				columns = GridCells.Adaptive(if (multiSelect) 128.dp else 96.dp),
				modifier = Modifier.fillMaxHeight(),
				contentPadding = contentPadding
			) {
				items(
					items = visibleItems,
					key = { it.id }
				) { item ->
					val interactionSource = remember { MutableInteractionSource() }

					Row(
						verticalAlignment = Alignment.CenterVertically
					) {
						if (multiSelect)
							Checkbox(
								checked = selection.contains(item),
								onCheckedChange = { checked ->
									if (checked) selection += item
									else selection -= item
								},
								interactionSource = interactionSource,
								enabled = item.timetableAllowed
							)

						Text(
							text = item.shortName,
							style = MaterialTheme.typography.bodyLarge,
							modifier = Modifier
								.clickable(
									interactionSource = interactionSource,
									indication = if (!multiSelect) LocalIndication.current else null,
									role = Role.Checkbox,
									enabled = item.timetableAllowed
								) {
									if (multiSelect) {
										if (selection.contains(item)) selection -= item
										else selection += item
									} else {
										onSelect(item)
										onDismiss(true)
									}
								}
								.weight(1f)
								.padding(
									vertical = 16.dp,
									horizontal = if (!multiSelect) 16.dp else 0.dp
								)
								.disabled(!item.timetableAllowed)
						)
					}
				}
			}
		}

		if (selectedType == null) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Icon(
					imageVector = Icons.Outlined.Info,
					contentDescription = null,
					modifier = Modifier
						.size(96.dp)
						.padding(bottom = 24.dp)
				)
				Text(stringResource(R.string.elementpicker_timetable_select))
			}
		}
	}
}

@Composable
fun ElementPickerTypeSelection(
	selectedType: ElementType?,
	onTypeChange: (ElementType?) -> Unit
) {
	NavigationBarInset {
		NavigationBarItem(
			icon = {
				Icon(
					painterResource(id = R.drawable.core_ui_rooms),
					contentDescription = null
				)
			},
			label = { Text(stringResource(id = R.string.all_rooms)) },
			selected = selectedType == ElementType.ROOM,
			onClick = { onTypeChange(ElementType.ROOM) }
		)
	}
}
