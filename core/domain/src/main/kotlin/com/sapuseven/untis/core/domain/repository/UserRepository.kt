package com.sapuseven.untis.core.domain.repository

import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.flow.Flow

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
	 * @param userId The ID of the user to switch to.
	 */
	suspend fun switchUser(userId: Long?)

	/**
	 * Deletes a user from the database.
	 * If the user to delete is active,
	 * it will switch to the first available user or clear the active user.
	 *
	 * @param userId The ID of the user to delete.
	 */
	suspend fun deleteUser(userId: Long)

	/**
	 * Updates a user in the database.
	 * Can be used to store a new user or to update an existing one.
	 *
	 * @param user An instance of the user to add. If the ID is 0, a new user will be created.
	 */
	suspend fun updateUser(user: User): Long
}
