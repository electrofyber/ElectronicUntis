package com.sapuseven.untis.ui

//import com.sapuseven.untis.feature.timetable.navigation.TimetableRoute
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sapuseven.untis.feature.login.navigation.LoginRoute
import com.sapuseven.untis.navigation.AppNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
	appState: MainAppState,
	modifier: Modifier = Modifier,
	//windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
	//key(appState.user.id) {
		AppNavHost(
			modifier = modifier,
			navController = appState.navController,
			startDestination = LoginRoute
		)
	//}

	/*val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	LaunchedEffect(globalSettings) {
		globalSettings.getSettings().first().let {
			if (!it.errorReportingSet) {
				bottomSheetState.show()
			}
		}
	}
	ReportsInfoBottomSheet(bottomSheetState) {
		scope.launch {
			globalSettings.updateSettings {
				errorReportingSet = true
			}
			bottomSheetState.hide()
		}
	}*/
}
