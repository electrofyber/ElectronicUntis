package com.sapuseven.untis.feature.timetable.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
	private val userRepository: UserRepository
) : ViewModel() {
	val uiState: StateFlow<UserListUiState> = userRepository.observeAllUsers()
		.map { UserListUiState(users = it) }
		.stateIn(
			scope = viewModelScope,
			initialValue = UserListUiState(),
			started = SharingStarted.WhileSubscribed(5_000),
		)

	fun deleteUser(userId: Long) = viewModelScope.launch {
		userRepository.deleteUser(userId)
	}
}

data class UserListUiState(
	val users: List<User> = emptyList()
)
