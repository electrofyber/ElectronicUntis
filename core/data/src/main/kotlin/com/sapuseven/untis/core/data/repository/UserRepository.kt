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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
	/**
	 * Returns the currently active user, or throws an exception if no user is active.
	 *
	 * @throws IllegalStateException If no user is active.
	 */
	fun getActiveUser(): User

	/**
	 * Observes the currently active user.
	 * It will not emit a value until the user state is loaded.
	 *
	 * If no user is active, it will emit `null`. This can also happen if the last active user was deleted.
	 *
	 * @return A flow that emits the currently active user, or `null` if no user is active.
	 */
	fun observeActiveUser(): Flow<User?>

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
	private val userStateFlow = observeActiveUser()
		.stateIn(
			scope = appScope,
			initialValue = null,
			started = SharingStarted.Eagerly
		)

	override fun getActiveUser(): User {
		return userStateFlow.value ?: throw IllegalStateException("No user is currently active.")
	}

	override fun observeActiveUser(): Flow<User?> =
		settingsDataStore.data.map { globalSettings ->
			globalSettings.activeUser.takeIf { globalSettings.hasActiveUser() }
				?.let { activeUserId ->
					getUserById(activeUserId) ?: run {
						userDao.getAllFlow().first().firstOrNull()?.toDomain()?.also {
							switchUser(it.id)
						}
					}
				}
		}.distinctUntilChanged()

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
