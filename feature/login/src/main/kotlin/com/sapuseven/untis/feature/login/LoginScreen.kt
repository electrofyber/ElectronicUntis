package com.sapuseven.untis.feature.login

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.sapuseven.untis.core.model.School
import com.sapuseven.untis.feature.login.schoolsearch.SchoolSearch
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
	onBackClick: () -> Unit,
	onDemoClick: () -> Unit,
	onManualDataInputClick: () -> Unit,
	onSchoolSelected: (School) -> Unit,
	onSetSchoolUri: (String) -> Unit,
	viewModel: LoginViewModel = hiltViewModel()
) {
	val focusManager = LocalFocusManager.current
	viewModel.setCodeScanLauncher(rememberLauncherForActivityResult(ScanContract()) {
		onSetSchoolUri(it.contents)
	})

	LaunchedEffect(Unit) {
		viewModel.events.collectLatest { event ->
			when (event) {
				LoginEvents.ClearFocus -> {
					focusManager.clearFocus()
				}
			}
		}
	}

	BackHandler(viewModel.searchMode) {
		viewModel.disableSearchMode()
	}

	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = { Text(stringResource(id = com.sapuseven.untis.core.ui.R.string.app_name)) },
				actions = {
					IconButton(onClick = { viewModel.onCodeScanClick(onSetSchoolUri) }) {
						Icon(
							painter = painterResource(id = R.drawable.feature_login_scan_code),
							contentDescription = stringResource(id = com.sapuseven.untis.core.ui.R.string.login_scan_code)
						)
					}
				},
				navigationIcon = {
					if (viewModel.shouldShowBackButton.value)
						IconButton(onClick = onBackClick) {
							Icon(
								imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
								contentDescription = stringResource(id = com.sapuseven.untis.core.ui.R.string.all_back)
							)
						}
				},
				colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
					containerColor = Color.Transparent,
					scrolledContainerColor = Color.Transparent
				)
			)
		},
		contentWindowInsets = WindowInsets.safeDrawing
	) { innerPadding ->
		val schoolSearchText = viewModel.schoolSearchText.collectAsState("")

		Column(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
		) {
			if (!viewModel.searchMode) Column(
				verticalArrangement = Arrangement.Center,
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f)
			) {
				Icon(
					painter = painterResource(id = R.drawable.feature_login_logo_student),
					contentDescription = null,
					tint = MaterialTheme.colorScheme.primary,
					modifier = Modifier
						.width(dimensionResource(id = R.dimen.feature_login_size_icon))
						.height(dimensionResource(id = R.dimen.feature_login_size_icon))
						.align(Alignment.CenterHorizontally)
						.padding(bottom = dimensionResource(id = R.dimen.feature_login_margin_pleaselogin_top))
				)
				Text(
					text = stringResource(id = com.sapuseven.untis.core.ui.R.string.login_welcome),
					style = MaterialTheme.typography.headlineLarge,
					textAlign = TextAlign.Center,
					modifier = Modifier.fillMaxWidth()
				)
			} else {
				SchoolSearch(
					modifier = Modifier
						.fillMaxWidth()
						.weight(1f),
					searchText = schoolSearchText.value,
					onSchoolSelected = onSchoolSelected
				)
			}

			Column(
				modifier = Modifier.fillMaxWidth()
			) {
				OutlinedTextField(
					value = schoolSearchText.value,
					onValueChange = { viewModel.updateSchoolSearchText(it) },
					singleLine = true,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = dimensionResource(id = R.dimen.feature_login_margin_input_horizontal))
						.onFocusChanged { viewModel.onSchoolSearchFocusChanged(it.isFocused) }
						.then(
							if (viewModel.searchMode) Modifier.padding(
								bottom = dimensionResource(
									id = R.dimen.feature_login_margin_input_horizontal
								)
							)
							else Modifier
						),
					label = {
						Text(stringResource(id = com.sapuseven.untis.core.ui.R.string.login_search_by_school_name_or_address))
					}
				)

				AnimatedVisibility(!viewModel.searchMode) {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(
								horizontal = dimensionResource(id = R.dimen.feature_login_margin_input_horizontal),
								vertical = dimensionResource(id = R.dimen.feature_login_margin_input_vertical)
							), horizontalArrangement = Arrangement.SpaceBetween
					) {
						TextButton(onClick = { onDemoClick() }) {
							Text(text = stringResource(id = com.sapuseven.untis.core.ui.R.string.login_demo))
						}

						TextButton(onClick = { onManualDataInputClick() }) {
							Text(text = stringResource(id = com.sapuseven.untis.core.ui.R.string.login_manual_data_input))
						}
					}
				}
			}
		}
	}
}
