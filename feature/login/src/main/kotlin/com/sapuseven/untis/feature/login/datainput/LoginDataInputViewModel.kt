package com.sapuseven.untis.feature.login.datainput

import android.util.Log
import android.util.Patterns
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.sapuseven.untis.core.api.exception.UntisApiException
import com.sapuseven.untis.core.api.model.response.UntisErrorCode
import com.sapuseven.untis.core.api.model.untis.SchoolInfo
import com.sapuseven.untis.core.data.repository.UserRepository
import com.sapuseven.untis.core.domain.LoginAndSaveUserUseCase
import com.sapuseven.untis.core.model.School
import com.sapuseven.untis.core.model.User
import com.sapuseven.untis.core.ui.R
import com.sapuseven.untis.feature.login.CodeScanService
import com.sapuseven.untis.feature.login.navigation.LoginDataInputRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val DEMO_API_URL = "https://api.sapuseven.com/untis/testing"

sealed class ExistingDataSource {
	data class ExistingUser(val userId: Long) : ExistingDataSource()
	data class FromSchoolSearch(val schoolInfo: SchoolInfo) : ExistingDataSource()
	object Demo : ExistingDataSource()
}

@HiltViewModel
class LoginDataInputViewModel @Inject constructor(
	private val loginUseCase: LoginAndSaveUserUseCase,
	private val userRepository: UserRepository,
	private val codeScanService: CodeScanService,
	savedStateHandle: SavedStateHandle,
) : ViewModel() {
	private val args = savedStateHandle.toRoute<LoginDataInputRoute>()

	private val existingUserId = if (args.userId == -1L) null else args.userId

	val isExistingUser = existingUserId != null

	val useStoredPassword
		get() = isExistingUser && loginData.password.value.isNullOrEmpty() && loginData.storedPassword != null

	val loginData = LoginData()

	var advanced by mutableStateOf(false)

	var searchMode by mutableStateOf(false)

	var validate by mutableStateOf(false)
		private set

	var loading by mutableStateOf(false)
		private set

	var errorText: Int? by mutableStateOf(null)
		private set

	var errorTextRaw: String? by mutableStateOf(null)
		private set

	var showQrCodeErrorDialog by mutableStateOf(false)
		private set

	var showSecondFactorInput by mutableStateOf(false)
		private set

	val showProfileUpdate = args.showProfileUpdate

	var schoolIdLocked by mutableStateOf(false)

	val schoolNameValid = derivedStateOf {
		loginData.schoolName.value?.isNotEmpty() ?: false
	}

	val usernameValid = derivedStateOf {
		loginData.username.value?.isNotEmpty() ?: false || (loginData.anonymous.value == true)
	}

	val apiUrlValid = derivedStateOf {
		loginData.apiUrl.value?.let {
			it.isEmpty() || Patterns.WEB_URL.matcher(it).matches()
		} ?: true
	}

	val codeScanResultHandler: (String) -> Unit = {
		try {
			loadFromSetSchoolUri(it)
			login()
		} catch (_: Exception) {
			showQrCodeErrorDialog = true
		}
	}

	init {
		viewModelScope.launch {
			existingUserId?.let { userRepository.getUserById(it) }?.let {
				loginData.loadFromUser(it)
				advanced = loginData.apiUrl.value?.isNotEmpty() == true
			}
		}

		args.schoolName?.let {
			loginData.schoolName.value = it
			schoolIdLocked = true
		}

		if (args.demoSchool) {
			loginData.anonymous.value = true
			loginData.schoolName.value = "demo"
			advanced = true
			loginData.apiUrl.value = DEMO_API_URL
		}

		args.autoLoginData?.let(codeScanResultHandler)

		if (args.autoLogin) {
			login()
		}
	}

	fun setCodeScanLauncher(launcher: ManagedActivityResultLauncher<ScanOptions, ScanIntentResult>) {
		codeScanService.setLauncher(launcher)
	}

	fun onLoginClick() {
		validate = true
		if (schoolNameValid.value && usernameValid.value && apiUrlValid.value) {
			errorText = null
			errorTextRaw = null
			login()
		}
	}

	private fun loadFromSetSchoolUri(data: String?) {
		if (data == null) return
		val appLinkData = data.toUri()

		if (appLinkData.isHierarchical && appLinkData.scheme == "untis" && appLinkData.host == "setschool") {
			// Untis-native values
			loginData.schoolName.value = appLinkData.getQueryParameter("school")
			loginData.username.value = appLinkData.getQueryParameter("user")
			loginData.password.value = appLinkData.getQueryParameter("key")

			// Custom values
			loginData.anonymous.value = appLinkData.getBooleanQueryParameter("anonymous", false)
			loginData.apiUrl.value = appLinkData.getQueryParameter("apiUrl")

			advanced = loginData.apiUrl.value?.isNotEmpty() == true
		} else {
			showQrCodeErrorDialog = true
		}
	}

	private fun login() = viewModelScope.launch {
		loading = true

		val anonymous = loginData.anonymous.value == true
		with(loginData) {
			loginUseCase(
				schoolName.value.orEmpty(),
				profileName.value,
				username.value.takeIf { !anonymous },
				password.value.takeIf { !anonymous },
				secondFactor.value,
				apiUrl.value.takeIf { advanced }
			)
		}.fold(
			onSuccess = {
				// TODO: Go to timetable
			},
			onFailure = { e ->
				if (e is UntisApiException) {
					Log.e(LoginDataInputViewModel::class.simpleName, "loadData Untis error", e)

					val errorTextRes: Int? = null// TODO ErrorMessageDictionary.getErrorMessageResource(e.error?.code, false)
					errorText = errorTextRes ?: R.string.errormessagedictionary_generic
					if (e.error?.code == UntisErrorCode.REQUIRE2_FACTOR_AUTHENTICATION_TOKEN) {
						showSecondFactorInput = true
					} else {
						errorTextRaw = when (e.error?.code) {
							else -> if (errorTextRes == null) e.error?.message else null
						}
					}
				} else {
					Log.e(LoginDataInputViewModel::class.simpleName, "loadData error", e)
					errorText = R.string.errormessagedictionary_generic
					errorTextRaw = e.message
				}
			}
		)

		loading = false
	}

	fun disableSearchMode() {
		if (searchMode) searchMode = false
	}

	fun onQrCodeErrorDialogDismiss() {
		showQrCodeErrorDialog = false
	}

	fun onCodeScanClick() {
		codeScanService.scanCode(codeScanResultHandler)
	}

	fun selectSchool(it: School) {
		loginData.schoolName.value = it.name
		searchMode = false
	}

	class LoginData(
		initialProfileName: String? = null,
		initialSchoolName: String? = null,
		initialAnonymous: Boolean? = null,
		initialUsername: String? = null,
		initialApiUrl: String? = null,
	) {
		val profileName = mutableStateOf(initialProfileName)
		val schoolName = mutableStateOf(initialSchoolName)
		val anonymous = mutableStateOf(initialAnonymous)
		val username = mutableStateOf(initialUsername)
		val password = mutableStateOf<String?>(null)
		val secondFactor = mutableStateOf<String?>(null)
		val apiUrl = mutableStateOf(initialApiUrl)
		var storedPassword: String? = null

		fun loadFromUser(user: User) {
			profileName.value = user.displayName
			schoolName.value = user.school.name
			anonymous.value = user.anonymous
			username.value = user.user
			apiUrl.value = ""//TODO user.apiHost
			storedPassword = user.key
		}
	}
}
