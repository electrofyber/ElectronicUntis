package com.sapuseven.untis.core.ui.dialogs

/*TODO @OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileManagementDialog(
	userRepository: UserRepository,
	onEdit: (user: User?) -> Unit,
	onDismiss: () -> Unit
) {
	var dismissed by remember { mutableStateOf(false) }
	val users by userRepository.allUsersState.collectAsStateWithLifecycle()
	val context = LocalContext.current
	val scope = rememberCoroutineScope()

	var deleteDialog by rememberSaveable { mutableStateOf<User?>(null) }

	fun dismiss() {
		onDismiss()
		dismissed = true
	}

	BackHandler(
		enabled = !dismissed,
	) {
		dismiss()
	}

	AppScaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = { Text(stringResource(id = R.string.core_ui_profile_edit)) },
				navigationIcon = {
					IconButton(onClick = {
						dismiss()
					}) {
						Icon(
							imageVector = Icons.Outlined.Close,
							contentDescription = stringResource(id = R.string.all_close)
						)
					}
				}
			)
		},
	) { innerPadding ->
		LazyColumn(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize(),
			contentPadding = insetsPaddingValues()
		) {
			item {
				ListItem(
					headlineContent = { Text(stringResource(R.string.core_ui_profile_edit_hint)) },
					leadingContent = {
						Icon(
							imageVector = Icons.Outlined.Info,
							contentDescription = null
						)
					}
				)

				HorizontalDivider(Modifier, DividerDefaults.Thickness, color = MaterialTheme.colorScheme.outline)
			}

			items(users) { user ->
				ListItem(
					headlineContent = { Text(user.getDisplayedName()) },
					supportingContent = { Text(user.userData.schoolName) },
					leadingContent = {
						Icon(
							imageVector = Icons.Outlined.Person,
							contentDescription = null
						)
					},
					trailingContent = {
						IconButton(onClick = {
							deleteDialog = user
						}) {
							Icon(
								imageVector = Icons.Outlined.Delete,
								contentDescription = stringResource(id = R.string.logindatainput_delete)
							)
						}
					},
					modifier = Modifier.clickable {
						onEdit(user)
					}
				)
			}

			item {
				ListItem(
					headlineContent = { Text(stringResource(id = R.string.core_ui_profile_add)) },
					leadingContent = {
						Icon(
							imageVector = Icons.Outlined.Add,
							contentDescription = null
						)
					},
					modifier = Modifier.clickable {
						onEdit(null)
					}
				)
			}
		}

		deleteDialog?.let { user ->
			AlertDialog(
				onDismissRequest = {
					deleteDialog = null
				},
				title = {
					Text(stringResource(id = R.string.core_ui_profile_dialog_delete_title))
				},
				text = {
					Text(
						stringResource(
							id = R.string.core_ui_profile_dialog_delete_message,
							user.getDisplayedName(context),
							user.userData.schoolName
						)
					)
				},
				confirmButton = {
					TextButton(
						onClick = {
							scope.launch {
								userRepository.deleteUser(user)
								deleteDialog = null
							}
						}) {
						Text(stringResource(id = R.string.core_ui_button_delete))
					}
				},
				dismissButton = {
					TextButton(
						onClick = {
							deleteDialog = null
						}) {
						Text(stringResource(id = R.string.core_ui_button_cancel))
					}
				}
			)
		}
	}
}*/
