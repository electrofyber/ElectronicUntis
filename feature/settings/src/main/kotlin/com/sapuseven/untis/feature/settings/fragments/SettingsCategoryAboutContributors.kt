package com.sapuseven.untis.feature.settings.fragments

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sapuseven.untis.feature.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCategoryAboutContributors(navController: NavController) {
	val uriHandler = LocalUriHandler.current
	val colorScheme = MaterialTheme.colorScheme
	val viewModel =
		hiltViewModel<SettingsViewModel, SettingsViewModel.Factory>(creationCallback = { factory ->
			factory.create(colorScheme)
		})

	/*val contributors by viewModel.contributors.collectAsStateWithLifecycle()
	val contributorsError by viewModel.contributorsError.collectAsStateWithLifecycle()

	AppScaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(stringResource(id = R.string.feature_settings_preference_info_contributors))
				},
				navigationIcon = {
					IconButton(onClick = { navController.navigateUp() }) {
						Icon(
							imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
							contentDescription = stringResource(id = R.string.all_back)
						)
					}
				}
			)
		}
	) { innerPadding ->
		Box(
			modifier = Modifier
				.padding(innerPadding)
		) {
			AnimatedVisibility(
				contributorsError == null,
				enter = fadeIn(),
				exit = fadeOut()
			) {
				LazyColumn(
					modifier = Modifier.fillMaxSize(),
					contentPadding = insetsPaddingValues()
				) {
					items(if (contributors.isEmpty()) 20 else contributors.size) {
						val user = contributors.getOrNull(it)
						Contributor(
							githubUser = user,
							onClick = user?.htmlUrl?.let { { uriHandler.openUri(it) } }
						)
					}
				}
			}

			AnimatedVisibility(
				contributorsError != null,
				enter = fadeIn() + expandVertically(),
				exit = shrinkVertically() + fadeOut()
			) {
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
					messageText = R.string.feature_settings_preference_info_contributors_error,
					messageTextRaw = contributorsError?.localizedMessage
				)
			}
		}
	}*/

	LaunchedEffect(Unit) {
		viewModel.loadContributors()
	}
}
