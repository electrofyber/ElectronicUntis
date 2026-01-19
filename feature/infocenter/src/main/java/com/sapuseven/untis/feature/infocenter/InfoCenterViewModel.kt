package com.sapuseven.untis.feature.infocenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapuseven.untis.core.domain.infocenter.GetAbsencesUseCase
import com.sapuseven.untis.core.domain.infocenter.GetDirectMessagesUseCase
import com.sapuseven.untis.core.domain.infocenter.GetExamsUseCase
import com.sapuseven.untis.core.domain.infocenter.GetHomeworkUseCase
import com.sapuseven.untis.core.domain.infocenter.GetMessagesOfDayUseCase
import com.sapuseven.untis.core.domain.infocenter.GetOfficeHoursUseCase
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.absences.Excuse
import com.sapuseven.untis.core.model.user.UserRight
import com.sapuseven.untis.feature.infocenter.pages.AbsencesUiState
import com.sapuseven.untis.feature.infocenter.pages.EventsUiState
import com.sapuseven.untis.feature.infocenter.pages.Message
import com.sapuseven.untis.feature.infocenter.pages.MessagesUiState
import com.sapuseven.untis.feature.infocenter.pages.OfficeHoursUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoCenterViewModel @Inject constructor(
	userRepository: UserRepository,
	//internal val masterDataRepository: MasterDataRepository,
	//internal val userSettingsRepository: UserSettingsRepository,
	private val getDirectMessages: GetDirectMessagesUseCase,
	getMessagesOfDay: GetMessagesOfDayUseCase,
	getExams: GetExamsUseCase,
	getHomework: GetHomeworkUseCase,
	getAbsences: GetAbsencesUseCase,
	getOfficeHours: GetOfficeHoursUseCase,
) : ViewModel() {
	private val excuses =
		emptyList<Excuse>()//TODO masterDataRepository.userData?.excuseStatuses ?: emptyList()

	private val currentUser = userRepository.getActiveUser()

	val shouldShowAbsences: Boolean
		get() = currentUser.rights.contains(UserRight.R_MY_ABSENCES)
	val shouldShowAbsencesAdd: Boolean
		get() = currentUser.rights.contains(UserRight.W_OWN_ABSENCE)
	val shouldShowAbsencesAddReason: Boolean
		get() = currentUser.rights.contains(UserRight.W_OWN_ABSENCEREASON)

	val shouldShowOfficeHours: Boolean
		get() = currentUser.rights.contains(UserRight.R_OFFICEHOURS)

	val messagesState: StateFlow<MessagesUiState> = combine(
		getMessagesOfDay(),
		getDirectMessages()
	) { day, direct ->
		MessagesUiState.Success(
			errors = listOfNotNull(day.exceptionOrNull(), direct.exceptionOrNull()),
			messages = day.getOrDefault(emptyList()).map(Message::Day) +
				direct.getOrDefault(emptyList()).map(Message::Direct)
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = MessagesUiState.Loading
	)

	private val _selectedMessage = MutableStateFlow<Message?>(null)
	val selectedMessage: StateFlow<Message?> = _selectedMessage
	private val _selectedMessageContent = MutableStateFlow<String?>(null)
	val selectedMessageContent: StateFlow<String?> = _selectedMessageContent
	private val _selectedMessageError = MutableStateFlow<String?>(null)
	val selectedMessageError: StateFlow<String?> = _selectedMessageError

	fun onMessageClicked(message: Message) {
		_selectedMessage.value = message
		_selectedMessageContent.value = null
		_selectedMessageError.value = null
		viewModelScope.launch {
			getDirectMessages(
				message.id.removePrefix("direct-").toLong()
			) // TODO this is pretty unreliable...
				.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
				.collect { result ->
					result?.fold(
						onSuccess = { _selectedMessageContent.value = it.body },
						onFailure = {
							//TODO Sentry.captureException(it)
							_selectedMessageError.value = it.message ?: ""
						}
					)
				}
		}
	}

	fun onMessageDismiss() {
		_selectedMessage.value = null
	}

	fun onMessageReply() {
		_selectedMessage.value = null
	}

	fun onMessageDelete() {
		_selectedMessage.value = null
	}

	val eventsState: StateFlow<EventsUiState> = combine(
		getExams(),
		getHomework(),
		EventsUiState::Success
	).stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = EventsUiState.Loading
	)

	val absencesState: StateFlow<AbsencesUiState> =
		(if (!shouldShowAbsences) emptyFlow() else getAbsences())
			.map { AbsencesUiState.Success(it, excuses) }
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = AbsencesUiState.Loading
			)

	val officeHoursState: StateFlow<OfficeHoursUiState> =
		(if (!shouldShowAbsences) emptyFlow() else getOfficeHours())
			.map(OfficeHoursUiState::Success)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = OfficeHoursUiState.Loading
			)
}
