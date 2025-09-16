package com.sapuseven.untis.core.data.repository

import androidx.datastore.core.DataStore
import com.sapuseven.untis.core.data.di.ApplicationScope
import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.data.mapper.toEntity
import com.sapuseven.untis.core.database.entity.UserDao
import com.sapuseven.untis.core.database.entity.UserEntity
import com.sapuseven.untis.core.datastore.model.Settings
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.masterdata.MasterData
import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

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
		.combine(observeAllUsers()) { activeUserId, allUsers ->
			allUsers.find { it.id == activeUserId }
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
		// TODO userDao.insertMasterData(userId, masterData)
		return userId
	}
}
