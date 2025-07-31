package com.sapuseven.untis.feature.login.datainput

import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.sapuseven.untis.core.api.exception.UntisApiException
import com.sapuseven.untis.core.api.model.response.UntisErrorCode
import com.sapuseven.untis.core.data.repository.UserRepository
import com.sapuseven.untis.core.domain.LoginAndSaveUserUseCase
import com.sapuseven.untis.core.ui.R
import com.sapuseven.untis.feature.login.CodeScanService
import com.sapuseven.untis.feature.login.navigation.LoginDataInputRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val DEMO_API_URL = "https://api.sapuseven.com/untis/testing"

@HiltViewModel
class LoginDataInputViewModel @Inject constructor(
	private val loginUseCase: LoginAndSaveUserUseCase,
	private val userRepository: UserRepository,
	private val codeScanService: CodeScanService,
	savedStateHandle: SavedStateHandle,
) : ViewModel() {
	private val args = savedStateHandle.toRoute<LoginDataInputRoute>()
	private val existingUserId = args.userId.takeIf { it != -1L }

	private val _uiState = MutableStateFlow(
		LoginDataInputUiState(
			isExistingUser = existingUserId != null,
			formData = LoginData(
				schoolName = args.schoolName ?: "",
				anonymous = args.demoSchool,
				apiUrl = if (args.demoSchool) DEMO_API_URL else ""
			),
			isSchoolNameLocked = args.schoolName != null
		)
	)
	val uiState: StateFlow<LoginDataInputUiState> = _uiState

	init {
		existingUserId?.let { id ->
			viewModelScope.launch {
				userRepository.getUserById(id)?.let { user ->
					_uiState.update { it.withLoadedUser(user) }
				}
			}
		}

		args.autoLoginData?.let(::onCodeScanned)
		if (args.autoLogin) login()
	}

	val onProfileNameChanged = { newValue: String -> updateForm { it.copy(profileName = newValue) } }
	val onSchoolNameChanged = { value: String -> updateForm { it.copy(schoolName = value) } }
	val onAnonymousToggled = { newValue: Boolean -> updateForm { it.copy(anonymous = newValue) } }
	val onUsernameChanged = { newValue: String -> updateForm { it.copy(username = newValue) } }
	val onPasswordChanged = { newValue: String -> updateForm { it.copy(password = newValue) } }
	val onSecondFactorChanged = { newValue: String -> updateForm { it.copy(secondFactor = newValue) } }
	val onApiUrlChanged = { newValue: String -> updateForm { it.copy(apiUrl = newValue) } }

	private fun updateForm(transform: (LoginData) -> LoginData) {
		_uiState.update { it.copy(formData = transform(it.formData), errorText = null) }
	}

	fun onCodeScanned(value: String) {
		tryParseLoginUri(value)?.let { loginUriData ->
			_uiState.update { it.copy(formData = loginUriData) }
			login()
		} ?: run {
			_uiState.update { it.copy(showQrError = true) }
		}
	}

	fun setCodeScanLauncher(launcher: ManagedActivityResultLauncher<ScanOptions, ScanIntentResult>) {
		codeScanService.setLauncher(launcher)
	}

	fun onLoginClick() {
		_uiState.update { it.copy(validate = true, errorText = null, errorTextRaw = null) }
		if (_uiState.value.formData.isValid) login()
	}

	private fun tryParseLoginUri(data: String): LoginData? {
		val appLinkData = data.toUri()

		return if (appLinkData.isHierarchical && appLinkData.scheme == "untis" && appLinkData.host == "setschool") {
			LoginData(
				// Untis-native values
				schoolName = appLinkData.getQueryParameter("school") ?: "",
				username = appLinkData.getQueryParameter("user") ?: "",
				password = appLinkData.getQueryParameter("key") ?: "",

				// Custom values
				anonymous = appLinkData.getBooleanQueryParameter("anonymous", false),
				apiUrl = appLinkData.getQueryParameter("apiUrl") ?: "",
			)
		} else {
			null
		}
	}

	private fun login() = viewModelScope.launch {
		_uiState.update { it.copy(isLoading = true) }

		val anonymous = _uiState.value.formData.anonymous
		with(_uiState.value.formData) {
			loginUseCase(
				schoolName,
				profileName,
				username.takeIf { !anonymous },
				password.takeIf { !anonymous },
				secondFactor.takeIf { it.isNotEmpty() },
				apiUrl.takeIf { it.isNotBlank() && isApiUrlValid }
			)
		}.fold(
			onSuccess = {
				_uiState.update { it.copy(isLoggedIn = true) }
			},
			onFailure = { e ->
				_uiState.update { it.copy(isLoading = false) }

				if (e is UntisApiException) {
					Log.e(LoginDataInputViewModel::class.simpleName, "loadData Untis error", e)

					val errorTextRes: Int? =
						null// TODO ErrorMessageDictionary.getErrorMessageResource(e.error?.code, false)

					_uiState.update { it.copy(errorText = errorTextRes ?: R.string.errormessagedictionary_generic) }

					if (e.error?.code == UntisErrorCode.REQUIRE2_FACTOR_AUTHENTICATION_TOKEN) {
						_uiState.update { it.copy(isSecondFactorRequired = true) }
					} else {
						_uiState.update {
							it.copy(
								isSecondFactorRequired = false,
								errorTextRaw = when (e.error?.code) {
									else -> if (errorTextRes == null) e.error?.message else null
								}
							)
						}
					}
				} else {
					Log.e(LoginDataInputViewModel::class.simpleName, "loadData error", e)
					_uiState.update {
						it.copy(
							errorText = R.string.errormessagedictionary_generic,
							errorTextRaw = e.message
						)
					}
				}
			}
		)
	}

	fun dismissQrCodeError() {
		_uiState.update { it.copy(showQrError = false) }
	}

	fun onCodeScanClick() {
		codeScanService.scanCode { onCodeScanned(it) }
	}
}
