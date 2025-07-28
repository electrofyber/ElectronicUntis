package com.sapuseven.untis.core.data.repository

import androidx.datastore.core.DataStore
import com.sapuseven.untis.core.database.entity.User
import com.sapuseven.untis.core.database.entity.UserDao
import com.sapuseven.untis.core.datastore.model.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
	/**
	 * The currently active user, or null if no user is available or the active user is still loading.
	 *
	 * Since there is no distinction between "no user" and "loading user",
	 * prefer to use [userState] and handle each case accordingly,
	 * or just use [requireUser] from a coroutine context.
	 */
	val currentUser: User?

	/**
	 * The current user state, which can be one of:
	 * - [UserState.Loading] when the active user is being loaded.
	 * - [UserState.NoUsers] when there are no users available.
	 * - [UserState.User] when a user is currently active.
	 */
	val userState: StateFlow<UserState>

	/**
	 * A state flow that contains a list of all users.
	 */
	val allUsersState: StateFlow<List<User>>

	/**
	 * Returns the currently active user, or throws an exception if no user is available.
	 * If the user state is still loading, this will suspend until the user is loaded
	 * until a timeout is reached.
	 *
	 * @throws IllegalStateException If no user exists.
	 */
	suspend fun requireUser(): User

	/**
	 * Switches to another user.
	 * @param userId The ID of the user to switch to.
	 */
	suspend fun switchUser(userId: Long)

	/**
	 * Deletes a user from the database.
	 * @param user An instance of the user to delete.
	 */
	suspend fun deleteUser(user: User)
}

@Singleton
class UserRepositoryImpl @Inject constructor(
	private val userDao: UserDao,
	private val settingsDataStore: DataStore<Settings>,
) : UserRepository {
	private val _userState = MutableStateFlow<UserState>(UserState.Loading)
	override val userState: StateFlow<UserState> = _userState

	override val allUsersState: StateFlow<List<User>> = userDao.getAllFlow().stateIn(
		scope = CoroutineScope(Dispatchers.IO),
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = emptyList()
	)

	override val currentUser: User?
		get() = (_userState.value as? UserState.User)?.user

	init {
		loadActiveUser()
	}

	private fun loadActiveUser() {
		CoroutineScope(Dispatchers.IO).launch {
			switchUser(settingsDataStore.data.first().activeUser)
		}
	}

	fun switchUser(user: User) {
		_userState.value = UserState.User(user)
		CoroutineScope(Dispatchers.IO).launch {
			settingsDataStore.updateData { currentSettings ->
				currentSettings.toBuilder()
					.setActiveUser(user.id)
					.build()
			}
		}
	}

	override suspend fun requireUser(): User {
		return when (val state = withTimeout(1_000) {
			userState.first { it !is UserState.Loading }
		}) {
			is UserState.User -> state.user
			UserState.NoUsers -> error("There are no users available.")
			UserState.Loading -> error("Users are still loading.") // should never happen
		}
	}

	override suspend fun switchUser(userId: Long) {
		val user = userDao.getByIdAsync(userId)
			?: userDao.getAllFlow().first().firstOrNull()

		user?.let {
			switchUser(it)
		} ?: run {
			_userState.value = UserState.NoUsers
		}
	}

	override suspend fun deleteUser(user: User) {
		userDao.delete(user)

		val remainingUsers = userDao.getAllAsync()
		val currentState = _userState.value
		if (currentState is UserState.User && currentState.user == user) {
			if (remainingUsers.isEmpty()) {
				_userState.value = UserState.NoUsers
			} else {
				_userState.value = UserState.User(remainingUsers.first())
			}
		}
	}
}

sealed class UserState {
	data object Loading : UserState()
	data object NoUsers : UserState()
	data class User(val user: com.sapuseven.untis.core.database.entity.User) : UserState()
}
