package com.sapuseven.untis.feature.login.datainput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// TODO: Extract string resources
@Composable
internal fun ProfileUpdateScreen() {
	Surface {
		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.fillMaxSize()
		) {
			Text(
				text = "A new school year has begun.",
				style = MaterialTheme.typography.titleLarge,
				textAlign = TextAlign.Center,
				modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
			)
			Text(
				text = "Please wait, BetterUntis is loading the new timetable data.\nThis may take a moment.",
				style = MaterialTheme.typography.bodyMedium,
				textAlign = TextAlign.Center,
				modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
			)
			CircularProgressIndicator(
				modifier = Modifier.padding(top = 16.dp)
			)
		}
	}
}
