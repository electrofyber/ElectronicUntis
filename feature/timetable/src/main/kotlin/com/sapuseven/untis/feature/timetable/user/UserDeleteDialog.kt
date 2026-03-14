package com.sapuseven.untis.feature.timetable.user

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.electrofyber.untis.feature.timetable.R

@Composable
fun UserDeleteDialog(
	onConfirm: () -> Unit,
	onDismiss: () -> Unit
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(onClick = onConfirm) {
				Text(stringResource(com.sapuseven.untis.core.ui.R.string.all_delete))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(stringResource(com.sapuseven.untis.core.ui.R.string.all_cancel))
			}
		},
		title = { Text(stringResource(R.string.feature_timetable_dialog_user_delete_title)) },
		text = { Text(stringResource(R.string.feature_timetable_dialog_user_delete_message)) }
	)
}
