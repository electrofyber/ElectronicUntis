package com.sapuseven.untis.feature.login.datainput

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.journeyapps.barcodescanner.ScanContract
import com.sapuseven.untis.core.ui.R
import com.sapuseven.untis.core.ui.common.LabeledSwitch
import com.sapuseven.untis.core.ui.common.MessageBubble
import com.sapuseven.untis.core.ui.common.SmallCircularProgressIndicator
import com.sapuseven.untis.core.ui.common.ifNotNull
import com.sapuseven.untis.core.ui.functional.None
import com.sapuseven.untis.feature.login.schoolsearch.SchoolSearchResults
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginDataInputScreen(
	onBackClick: () -> Unit,
	onComplete: () -> Unit,
	viewModel: LoginDataInputViewModel = hiltViewModel()
) {
	viewModel.setCodeScanLauncher(rememberLauncherForActivityResult(ScanContract()) { viewModel.onCodeScanned(it.contents) })

	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	LaunchedEffect(uiState.isLoggedIn) {
		if (uiState.isLoggedIn) {
			onComplete()
		}
	}

	var showSchoolSearch by rememberSaveable { mutableStateOf(false) }
	var showAdvanced by rememberSaveable { mutableStateOf(false) }

	LaunchedEffect(Unit) {
		snapshotFlow { uiState.formData.apiUrl }.first { it.isNotEmpty() }
		showAdvanced = true
	}

	BackHandler(showSchoolSearch) {
		showSchoolSearch = false
	}

	if (uiState.showProfileUpdate)
		ProfileUpdateScreen()
	else
		Scaffold(
			contentWindowInsets = WindowInsets.None,
			floatingActionButtonPosition = FabPosition.End,
			floatingActionButton = {
				if (!showSchoolSearch) {
					ExtendedFloatingActionButton(
						modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
						containerColor = MaterialTheme.colorScheme.primary,
						icon = {
							if (uiState.isLoading)
								SmallCircularProgressIndicator(color = LocalContentColor.current)
							else
								Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null)
						},
						text = { Text(stringResource(id = R.string.logindatainput_login)) },
						onClick = { viewModel.onLoginClick() }
					)
				}
			},
			topBar = {
				CenterAlignedTopAppBar(
					title = {
						Text(
							if (uiState.isExistingUser)
								stringResource(id = R.string.logindatainput_title_edit)
							else
								stringResource(id = R.string.logindatainput_title_add)
						)
					},
					actions = {
						IconButton(onClick = { viewModel.onCodeScanClick() }) {
							Icon(
								painter = painterResource(id = com.sapuseven.untis.feature.login.R.drawable.feature_login_scan_code),
								contentDescription = stringResource(id = R.string.login_scan_code)
							)
						}
					},
					navigationIcon = {
						IconButton(onClick = onBackClick) {
							Icon(
								imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
								contentDescription = stringResource(id = R.string.all_back)
							)
						}
					}
				)
			}
		) { innerPadding ->
			val schoolNameFocusRequester = remember { FocusRequester() }
			val secondFactorFocusRequester = remember { FocusRequester() }
			val focusManager = LocalFocusManager.current

			LaunchedEffect(uiState.isSecondFactorRequired) {
				if (uiState.isSecondFactorRequired) secondFactorFocusRequester.requestFocus()
			}

			if (showSchoolSearch) {
				Column(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding)
						.consumeWindowInsets(innerPadding)
						.windowInsetsPadding(WindowInsets.safeDrawing)
				) {
					SchoolSearchResults(
						modifier = Modifier
							.fillMaxWidth()
							.weight(1f),
						query = uiState.formData.schoolName,
						onSchoolSelected = {
							viewModel.onSchoolNameChanged(it.name)
							showSchoolSearch = false
						}
					)

					InputField(
						value = uiState.formData.schoolName,
						onValueChange = viewModel.onSchoolNameChanged,
						label = { Text(stringResource(id = R.string.logindatainput_school)) },
						enabled = !uiState.isLoading,
						valid = !uiState.validate || uiState.formData.isSchoolNameValid,
						errorText = stringResource(id = R.string.logindatainput_error_field_empty),
						trailingIcon = {
							IconButton(
								enabled = !uiState.isLoading,
								onClick = { showSchoolSearch = true }
							) {
								Icon(
									imageVector = Icons.Outlined.Search,
									contentDescription = stringResource(R.string.login_search_by_school_name_or_address)
								)
							}
						},
						focusManager = focusManager,
						modifier = Modifier
							.focusRequester(schoolNameFocusRequester)
							.padding(bottom = 8.dp)
					)

					LaunchedEffect(Unit) {
						schoolNameFocusRequester.requestFocus()
					}
				}
			} else {
				Column(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding)
						.consumeWindowInsets(innerPadding)
						.windowInsetsPadding(WindowInsets.ime)
						.verticalScroll(rememberScrollState())
				) {
					if (LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE)
						Icon(
							painter = painterResource(id = com.sapuseven.untis.feature.login.R.drawable.feature_login_profile),
							contentDescription = null,
							tint = MaterialTheme.colorScheme.primary,
							modifier = Modifier
								.width(dimensionResource(id = com.sapuseven.untis.feature.login.R.dimen.feature_login_size_icon))
								.height(dimensionResource(id = com.sapuseven.untis.feature.login.R.dimen.feature_login_size_icon))
								.align(Alignment.CenterHorizontally)
								.padding(bottom = dimensionResource(id = com.sapuseven.untis.feature.login.R.dimen.feature_login_margin_pleaselogin_top))
						)

					// TODO: Better error text handling
					MessageBubble(
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = 16.dp, vertical = 8.dp),
						icon = {
							Icon(
								painter = painterResource(id = R.drawable.core_ui_error),
								contentDescription = stringResource(id = R.string.all_error)
							)
						},
						messageText = if (uiState.isSecondFactorRequired) R.string.errormessagedictionary_second_factor_requried else uiState.errorText,
						messageTextRaw = uiState.errorTextRaw
					)

					InputField(
						value = uiState.formData.profileName,
						onValueChange = viewModel.onProfileNameChanged,
						label = { Text(stringResource(id = R.string.logindatainput_profilename)) },
						enabled = !uiState.isLoading,
						focusManager = focusManager
					)
					InputField(
						value = uiState.formData.schoolName,
						onValueChange = viewModel.onSchoolNameChanged,
						label = { Text(stringResource(id = R.string.logindatainput_school)) },
						enabled = !uiState.isLoading,
						valid = !uiState.validate || uiState.formData.isSchoolNameValid,
						errorText = stringResource(id = R.string.logindatainput_error_field_empty),
						trailingIcon = {
							IconButton(
								enabled = !uiState.isLoading,
								onClick = { showSchoolSearch = true }
							) {
								Icon(
									imageVector = Icons.Outlined.Search,
									contentDescription = stringResource(R.string.login_search_by_school_name_or_address)
								)
							}
						},
						focusManager = focusManager
					)
					Spacer(
						modifier = Modifier.height(32.dp)
					)
					InputSwitch(
						value = uiState.formData.anonymous,
						onValueChange = viewModel.onAnonymousToggled,
						label = { Text(stringResource(id = R.string.logindatainput_anonymous_login)) },
						enabled = !uiState.isLoading
					)
					AnimatedVisibility(visible = !uiState.formData.anonymous) {
						Column {
							InputField(
								value = uiState.formData.username,
								onValueChange = viewModel.onUsernameChanged,
								label = { Text(stringResource(id = R.string.logindatainput_username)) },
								enabled = !uiState.isLoading,
								valid = !uiState.validate || uiState.formData.isUsernameValid,
								errorText = stringResource(id = R.string.logindatainput_error_field_empty),
								contentType = ContentType.Username,
								focusManager = focusManager
							)
							InputField(
								value = uiState.formData.password,
								onValueChange = viewModel.onPasswordChanged,
								type = KeyboardType.Password,
								label = {
									Text(
										if (uiState.formData.storedPassword != null)
											stringResource(id = R.string.logindatainput_key_saved)
										else
											stringResource(id = R.string.logindatainput_key)
									)
								},
								enabled = !uiState.isLoading,
								contentType = ContentType.Password,
								focusManager = focusManager
							)
							AnimatedVisibility(visible = uiState.isSecondFactorRequired) {
								InputField(
									value = uiState.formData.secondFactor,
									onValueChange = viewModel.onSecondFactorChanged,
									type = KeyboardType.NumberPassword,
									label = {
										Text(
											stringResource(id = R.string.logindatainput_2fa)
										)
									},
									enabled = !uiState.isLoading,
									focusManager = focusManager,
									modifier = Modifier
										.focusRequester(secondFactorFocusRequester)
								)
							}
							Spacer(
								modifier = Modifier.height(32.dp)
							)
						}
					}
					LabeledSwitch(
						label = { Text(stringResource(id = R.string.logindatainput_show_advanced)) },
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = 16.dp),
						checked = showAdvanced,
						onCheckedChange = { showAdvanced = it },
						enabled = !uiState.isLoading
					)
					AnimatedVisibility(visible = showAdvanced) {
						Column {
							// Proxy is not supported currently
							/*InputField(
								state = uiState.formData.proxyUrl,
								type = KeyboardType.Uri,
								label = { Text(stringResource(id = R.string.logindatainput_proxy_host)) },
								enabled = !viewModel.loading,
								valid = !viewModel.validate || viewModel.proxyUrlValid.value,
								errorText = stringResource(id = R.string.logindatainput_error_invalid_url)
							)*/
							InputField(
								value = uiState.formData.apiUrl,
								onValueChange = viewModel.onApiUrlChanged,
								type = KeyboardType.Uri,
								label = { Text(stringResource(id = R.string.logindatainput_api_url)) },
								enabled = !uiState.isLoading,
								valid = !uiState.validate || uiState.formData.isApiUrlValid,
								errorText = stringResource(id = R.string.logindatainput_error_invalid_url),
								focusManager = focusManager
							)
						}
					}

					Spacer(Modifier.height(80.dp)) // Space for FAB
					Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))

					if (uiState.showQrError) {
						AlertDialog(
							onDismissRequest = {
								viewModel.dismissQrCodeError()
							},
							title = {
								Text(stringResource(id = R.string.logindatainput_dialog_qrcodeinvalid_title))
							},
							text = {
								Text(stringResource(id = R.string.logindatainput_dialog_qrcodeinvalid_text))
							},
							confirmButton = {
								TextButton(
									onClick = {
										viewModel.dismissQrCodeError()
									}) {
									Text(stringResource(id = R.string.all_ok))
								}
							}
						)
					}
				}
			}
		}
}

@OptIn(
	ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
	ExperimentalFoundationApi::class
)
@Composable
private fun InputField(
	value: String,
	onValueChange: (String) -> Unit,
	type: KeyboardType = KeyboardType.Text,
	label: @Composable (() -> Unit)? = null,
	enabled: Boolean = true,
	valid: Boolean = true,
	errorText: String = "",
	contentType: ContentType? = null,
	trailingIcon: @Composable (() -> Unit)? = null,
	focusManager: FocusManager? = null,
	modifier: Modifier = Modifier
) {
	val bringIntoViewRequester = remember { BringIntoViewRequester() }
	val coroutineScope = rememberCoroutineScope()

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp)
			.bringIntoViewRequester(bringIntoViewRequester)
	) {
		OutlinedTextField(
			value = value,
			onValueChange = onValueChange,
			singleLine = true,
			keyboardOptions = KeyboardOptions(
				keyboardType = type,
				imeAction = if (focusManager == null) ImeAction.Unspecified else ImeAction.Next
			),
			keyboardActions = KeyboardActions(
				onNext = {
					focusManager?.moveFocus(FocusDirection.Next)
				}
			),
			visualTransformation = if (type == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None,
			label = label,
			enabled = enabled,
			isError = !valid,
			trailingIcon = trailingIcon,
			modifier = modifier
				.fillMaxWidth()
				.onFocusChanged {
					if (it.isFocused) {
						coroutineScope.launch {
							bringIntoViewRequester.bringIntoView()
						}
					}
				}
				.ifNotNull(contentType) {
					semantics { this.contentType = it }
				}
		)

		AnimatedVisibility(visible = !valid) {
			Text(
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
				color = MaterialTheme.colorScheme.error,
				style = MaterialTheme.typography.bodyMedium,
				text = errorText
			)
		}
	}
}

@Composable
private fun InputSwitch(
	value: Boolean,
	onValueChange: (Boolean) -> Unit,
	label: @Composable () -> Unit,
	enabled: Boolean = true
) {
	LabeledSwitch(
		label = label,
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
		checked = value,
		onCheckedChange = onValueChange,
		enabled = enabled
	)
}
