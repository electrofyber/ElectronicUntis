package com.sapuseven.untis.feature.login

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
	private val codeScanService: CodeScanService,
) : ViewModel() {
	private val _schoolSearchText = MutableStateFlow("")
	val schoolSearchText: StateFlow<String> = _schoolSearchText

	fun setCodeScanLauncher(launcher: ManagedActivityResultLauncher<ScanOptions, ScanIntentResult>) {
		codeScanService.setLauncher(launcher)
	}

	fun onCodeScanClick(onSuccess: (String) -> Unit) {
		codeScanService.scanCode(onSuccess)
	}
}
