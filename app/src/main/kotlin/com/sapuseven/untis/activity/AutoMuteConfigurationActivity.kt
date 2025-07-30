package com.sapuseven.untis.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.sapuseven.untis.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AutoMuteConfigurationActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContent {
			AppTheme {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					//TODO AutoMuteSettings() { finish() }
				} else {
					Text("Auto-Mute is not supported on this device.")
				}
			}
		}
	}
}
