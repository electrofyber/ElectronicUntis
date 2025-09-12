package com.sapuseven.untis.core.data.repository

import androidx.datastore.core.DataStore
import com.sapuseven.untis.core.api.model.untis.MasterData
import com.sapuseven.untis.core.data.di.ApplicationScope
import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.data.mapper.toEntity
import com.sapuseven.untis.core.database.entity.UserDao
import com.sapuseven.untis.core.database.entity.UserEntity
import com.sapuseven.untis.core.datastore.model.Settings
import com.sapuseven.untis.core.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
	/**
	 * Returns the currently active user, or throws an exception if no user is active.
	 *
	 * @throws IllegalStateException If no user is active.
	 * @see observeActiveUser
	 */
	fun getActiveUser(): User

	/**
	 * Returns all users in the database.
	 *
	 * If there are no users, an empty list is returned.
	 *
	 * @see observeAllUsers
	 */
	fun getAllUsers(): List<User>

	/**
	 * Observes the currently active user.
	 * It will not emit a value until the user state is loaded.
	 *
	 * If no user is active, it will emit `null`. This can also happen if the last active user was deleted.
	 *
	 * @return A flow that emits the currently active user, or `null` if no user is active.
	 * @see getActiveUser
	 */
	fun observeActiveUser(): Flow<User?>

	/**
	 * Observes all users in the database.
	 *
	 * If there are no users, it will emit an empty list.
	 *
	 * @return A flow that emits the list of all users whenever it changes.
	 * @see getAllUsers
	 */
	fun observeAllUsers(): Flow<List<User>>

	/**
	 * Switches to another user.
	 * @param userId The ID of the user to switch to.
	 */
	suspend fun getUserById(userId: Long): User?

	/**
	 * Switches the currently active user.
	 * If the user is `null`, it will clear the active user.
	 *
	 * @param userId The id of the user to switch to.
	 */
	suspend fun switchUser(userId: Long?)

	/**
	 * Deletes a user from the database.
	 * If the user to delete is active,
	 * it will switch to the first available user or clear the active user.
	 *
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
	@ApplicationScope appScope: CoroutineScope,
	private val userDao: UserDao,
	private val settingsDataStore: DataStore<Settings>,
) : UserRepository {
	private val allUsersStateFlow = observeAllUsers()
		.stateIn(appScope, SharingStarted.Eagerly, emptyList())

	private val userStateFlow = observeActiveUser()
		.stateIn(appScope, SharingStarted.Eagerly, null)

	override fun getActiveUser(): User =
		checkNotNull(userStateFlow.value) { "No user is currently active" }

	override fun getAllUsers(): List<User> = allUsersStateFlow.value

	override fun observeAllUsers(): Flow<List<User>> = userDao.getAllFlow()
		.map { entities -> entities.map(UserEntity::toDomain) }
		.distinctUntilChanged()

	override fun observeActiveUser(): Flow<User?> = settingsDataStore.data
		.map { globalSettings ->
			globalSettings.activeUser.takeIf { globalSettings.hasActiveUser() }
		}
		.combine(allUsersStateFlow.filterNotNull()) { activeUserId, allUsers ->
			allUsers.firstOrNull { it.id == activeUserId }
				?: allUsers.firstOrNull()?.also { switchUser(it.id) }
		}
		.distinctUntilChanged()

	override suspend fun switchUser(userId: Long?) {
		settingsDataStore.updateData { currentSettings ->
			currentSettings.toBuilder()
				.apply {
					userId?.let(::setActiveUser) ?: clearActiveUser()
				}
				.build()
		}
	}

	override suspend fun getUserById(userId: Long) = userDao.getByIdAsync(userId)?.toDomain()

	override suspend fun deleteUser(user: User) {
		userDao.delete(user.id)
		if (user == getActiveUser()) {
			val remainingUsers = userDao.getAllAsync().map(UserEntity::toDomain)
			if (remainingUsers.isEmpty()) {
				switchUser(remainingUsers.firstOrNull()?.id)
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
