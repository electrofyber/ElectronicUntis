package com.sapuseven.untis.feature.timetable.userlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun UserListDialogContent(
	onBackClick: () -> Unit,
	onUserEdit: (userId: Long?) -> Unit,
) {
	Box(
		contentAlignment = Alignment.Center,
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.surface)
	) {
		Text("Test!")
		Button(onClick = onBackClick) {
			Text("Go back")
		}
	}
}
