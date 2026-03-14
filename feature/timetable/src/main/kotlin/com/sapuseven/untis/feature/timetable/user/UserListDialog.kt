package com.sapuseven.untis.feature.timetable.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.electrofyber.untis.feature.timetable.R
import com.sapuseven.untis.core.ui.functional.None
import com.sapuseven.untis.core.ui.functional.insetsPaddingValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListDialogContent(
	viewModel: UserListViewModel = hiltViewModel(),
	onBackClick: () -> Unit,
	onUserEdit: (userId: Long?) -> Unit,
	onUserDelete: (userId: Long) -> Unit,
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = { Text(stringResource(id = R.string.feature_timetable_user_list)) },
				navigationIcon = {
					IconButton(onClick = onBackClick) {
						Icon(
							imageVector = Icons.Outlined.Close,
							contentDescription = stringResource(id = R.string.feature_timetable_close)
						)
					}
				}
			)
		},
		contentWindowInsets = WindowInsets.None
	) { innerPadding ->
		LazyColumn(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize(),
			contentPadding = insetsPaddingValues()
		) {
			item {
				ListItem(
					headlineContent = { Text(stringResource(R.string.feature_timetable_user_edit_hint)) },
					leadingContent = {
						Icon(
							imageVector = Icons.Outlined.Info,
							contentDescription = null
						)
					}
				)

				HorizontalDivider(
					Modifier,
					DividerDefaults.Thickness,
					color = MaterialTheme.colorScheme.outline
				)
			}

			items(uiState.users, key = { it.id }) { user ->
				ListItem(
					headlineContent = { Text(user.displayName) },
					supportingContent = { Text(user.school.displayName) },
					leadingContent = {
						Icon(
							imageVector = Icons.Outlined.Person,
							contentDescription = null
						)
					},
					trailingContent = {
						IconButton(onClick = {
							onUserDelete(user.id)
						}) {
							Icon(
								imageVector = Icons.Outlined.Delete,
								contentDescription = stringResource(id = R.string.feature_timetable_delete)
							)
						}
					},
					modifier = Modifier
						.animateItem()
						.clickable {
							onUserEdit(user.id)
						}
				)
			}

			item(key = 0) {
				ListItem(
					headlineContent = { Text(stringResource(id = R.string.feature_timetable_user_add)) },
					leadingContent = {
						Icon(
							imageVector = Icons.Outlined.Add,
							contentDescription = null
						)
					},
					modifier = Modifier
						.animateItem()
						.clickable {
							onUserEdit(null)
						}
				)
			}
		}
	}
}
