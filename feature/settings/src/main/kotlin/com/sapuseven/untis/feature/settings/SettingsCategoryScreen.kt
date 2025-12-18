package com.sapuseven.untis.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sapuseven.untis.core.ui.functional.None

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCategoryScreen(
	navController: NavHostController,
	title: String?,
	colorScheme: ColorScheme = MaterialTheme.colorScheme,
	viewModel: SettingsViewModel = hiltViewModel<SettingsViewModel, SettingsViewModel.Factory>(
		creationCallback = { factory -> factory.create(colorScheme) }
	),
	content: @Composable (SettingsViewModel) -> Unit
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(
						title
							?: stringResource(id = R.string.feature_settings_activity_title_settings)
					)
				},
				navigationIcon = {
					IconButton(onClick = { navController.navigateUp() }) {
						Icon(
							imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
							contentDescription = stringResource(id = com.sapuseven.untis.core.ui.R.string.all_back)
						)
					}
				},
				scrollBehavior = scrollBehavior
			)
		},
		contentWindowInsets = WindowInsets.None
	) { innerPadding ->
		Column(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.windowInsetsPadding(WindowInsets.safeDrawing)
		) {
			content(viewModel)
		}
	}
}
