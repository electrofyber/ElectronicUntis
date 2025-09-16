package com.sapuseven.untis.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sapuseven.untis.navigation.AppNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
	modifier: Modifier = Modifier,
	navController: NavHostController = rememberNavController(),
	startDestination: Any,
	//windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
	AppNavHost(
		modifier = modifier,
		navController = navController,
		startDestination = startDestination
	)

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
