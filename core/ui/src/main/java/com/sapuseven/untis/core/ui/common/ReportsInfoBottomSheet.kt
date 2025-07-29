package com.sapuseven.untis.core.ui.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsInfoBottomSheet(
	sheetState: SheetState,
	onSave: () -> Unit,
) {
	/*TODO val scope = rememberCoroutineScope()
	var saveEnabled by rememberSaveable { mutableStateOf(true) }

	if (sheetState.isVisible) {
		Box(
			modifier = Modifier.fillMaxSize()
		) {
			ModalBottomSheet(
				onDismissRequest = {
					scope.launch {
						sheetState.hide()
					}
				},
				sheetState = sheetState,
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween,
					modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
				) {
					Text(
						text = stringResource(R.string.main_dialog_reports_title),
						style = MaterialTheme.typography.headlineLarge
					)

					Icon(
						painter = painterResource(id = R.drawable.all_reports_image),
						modifier = Modifier
                            .size(72.dp)
                            .padding(start = 16.dp, end = 8.dp),
						contentDescription = null
					)
				}

				Text(
					text = stringResource(R.string.main_dialog_reports_info_1),
					modifier = Modifier
						.padding(horizontal = 16.dp)
				)

				Text(
					text = stringResource(R.string.preference_reports_info_desc),
					modifier = Modifier
						.padding(start = 16.dp, end = 16.dp, top = 8.dp)
				)

				Column(
					modifier = Modifier
						.padding(vertical = 16.dp)
				) {
					CompositionLocalProvider(
						LocalListItemColors provides ListItemDefaults.colors(
							containerColor = MaterialTheme.colorScheme.surfaceContainerLow
						)
					) {
						HorizontalDivider()

						SwitchPreference(
							title = { Text(stringResource(R.string.preference_reports_enable)) },
							settingsRepository = repository,
							value = { it.errorReportingEnable },
							onValueChange = { errorReportingEnable = it }
						)

						SwitchPreference(
							title = { Text(stringResource(R.string.preference_reports_breadcrumbs)) },
							summary = { Text(stringResource(R.string.preference_reports_breadcrumbs_desc)) },
							settingsRepository = repository,
							value = { it.errorReportingEnableBreadcrumbs },
							onValueChange = { errorReportingEnableBreadcrumbs = it },
							enabledCondition = { it.errorReportingEnable }
						)

						HorizontalDivider()
					}
				}

				Text(
					text = stringResource(R.string.main_dialog_reports_info_2),
					modifier = Modifier
						.padding(horizontal = 16.dp)
				)

				Row(
					horizontalArrangement = Arrangement.End,
					modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
				) {
					Button(
						enabled = saveEnabled,
						onClick = {
							saveEnabled = false
							onSave()
						}
					) {
						Text(text = stringResource(R.string.main_dialog_reports_save))
					}
				}
			}

			// Workaround for the modal bottom sheet not covering the nav bar
			Box(
				modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                    .fillMaxWidth()
                    .height(insetsPaddingValues().calculateBottomPadding())
                    .align(Alignment.BottomCenter)
			) {}
		}
	}*/
}
