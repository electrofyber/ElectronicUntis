package com.sapuseven.untis.core.data.repository

import androidx.datastore.core.DataStore
import com.sapuseven.untis.core.api.model.untis.MasterData
import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.data.mapper.toEntity
import com.sapuseven.untis.core.database.entity.UserDao
import com.sapuseven.untis.core.database.entity.UserEntity
import com.sapuseven.untis.core.datastore.model.Settings
import com.sapuseven.untis.core.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
	 * or just use [currentUser] from a coroutine context.
	 */
	val currentUser: User?

	/**
	 * The current user state, which can be one of:
	 * - [UserState.Loading] when the active user is being loaded.
	 * - [UserState.NoUsers] when there are no users available.
	 * - [UserState.ActiveUser] when a user is currently active.
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
	suspend fun currentUser(): User

	/**
	 * Switches to another user.
	 * @param userId The ID of the user to switch to.
	 */
	suspend fun getUserById(userId: Long): User?

	/**
	 * Switches the currently active user.
	 * @param userId The ID of the user to switch to.
	 */
	suspend fun switchUser(userId: Long)

	/**
	 * Deletes a user from the database.
	 * @param user An instance of the user to delete.
	 */
	suspend fun deleteUser(user: User)

	/**
	 * Updates a user in the database.
	 * Can be used to create a new user if the ID is 0.
	 * @param user An instance of the user to add.
	 * @param masterData The master data for the user.
	 */
	suspend fun updateUser(user: User, masterData: MasterData): Long
}

@Singleton
class UserRepositoryImpl @Inject constructor(
	private val userDao: UserDao,
	private val settingsDataStore: DataStore<Settings>,
) : UserRepository {
	private val _userState = MutableStateFlow<UserState>(UserState.Loading)
	override val userState: StateFlow<UserState> = _userState

	override val allUsersState: StateFlow<List<User>> = userDao.getAllFlow()
		.map { it.map(UserEntity::toDomain) }
		.stateIn(
			scope = CoroutineScope(Dispatchers.IO),
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = emptyList()
		)

	override val currentUser: User?
		get() = (_userState.value as? UserState.ActiveUser)?.user

	init {
		loadActiveUser()
	}

	private fun loadActiveUser() {
		CoroutineScope(Dispatchers.IO).launch {
			switchUser(settingsDataStore.data.first().activeUser)
		}
	}

	fun switchUser(user: User) {
		_userState.value = UserState.ActiveUser(user)
		CoroutineScope(Dispatchers.IO).launch {
			settingsDataStore.updateData { currentSettings ->
				currentSettings.toBuilder()
					.setActiveUser(user.id)
					.build()
			}
		}
	}

	override suspend fun currentUser(): User {
		return when (val state = withTimeout(1_000) {
			userState.first { it !is UserState.Loading }
		}) {
			is UserState.ActiveUser -> state.user
			UserState.NoUsers -> error("There are no users available.")
			UserState.Loading -> error("Users are still loading.") // should never happen
		}
	}

	override suspend fun getUserById(userId: Long) = userDao.getByIdAsync(userId)?.toDomain()

	override suspend fun switchUser(userId: Long) {
		val user = (userDao.getByIdAsync(userId) ?: userDao.getAllFlow().first().firstOrNull())?.toDomain()

		user?.let {
			switchUser(it)
		} ?: run {
			_userState.value = UserState.NoUsers
		}
	}

	override suspend fun deleteUser(user: User) {
		userDao.delete(user.id)

		val remainingUsers = userDao.getAllAsync()
		val currentState = _userState.value
		if (currentState is UserState.ActiveUser && currentState.user == user) {
			if (remainingUsers.isEmpty()) {
				_userState.value = UserState.NoUsers
			} else {
				_userState.value = UserState.ActiveUser(remainingUsers.first().toDomain())
			}
		}
	}

	override suspend fun updateUser(
		user: User,
		masterData: MasterData
	): Long {
		val userId = user.id.takeIf { it > 0L }?.also {
			userDao.update(user.toEntity())
		} ?: run {
			userDao.insert(user.toEntity())
		}

		userDao.deleteUserData(userId)
		userDao.insertMasterData(userId, masterData)
		return userId
	}
}

sealed class UserState {
	data object Loading : UserState()
	data object NoUsers : UserState()
	data class ActiveUser(val user: User) : UserState()
}
