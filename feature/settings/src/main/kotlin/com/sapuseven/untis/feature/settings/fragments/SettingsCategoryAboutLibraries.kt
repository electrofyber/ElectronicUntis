package com.sapuseven.untis.feature.settings.fragments

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.sapuseven.untis.core.ui.functional.insetsPaddingValues
import com.sapuseven.untis.feature.settings.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCategoryAboutLibraries(navController: NavController) {
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(stringResource(id = R.string.feature_settings_preference_info_libraries))
				},
				navigationIcon = {
					IconButton(onClick = { navController.navigateUp() }) {
						Icon(
							imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
							contentDescription = stringResource(id = com.sapuseven.untis.core.ui.R.string.all_back)
						)
					}
				}
			)
		}
	) { innerPadding ->
		val libs by rememberLibraries()

		LibrariesContainer(
			libraries = libs,
			contentPadding = insetsPaddingValues(),
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
		)
	}
}
