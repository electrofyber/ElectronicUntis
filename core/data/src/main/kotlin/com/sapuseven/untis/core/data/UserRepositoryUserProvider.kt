package com.sapuseven.untis.core.data

import com.sapuseven.untis.core.data.repository.UserRepository
import com.sapuseven.untis.core.datastore.UserProvider
import javax.inject.Inject

// TODO: Figure out a good place to put this
class UserRepositoryUserProvider @Inject constructor(
	private val userRepository: UserRepository
) : UserProvider {
	override fun optionalUserId(): Long? {
		return userRepository.currentUser?.id
	}

	override suspend fun requireUserId(): Long {
		return userRepository.requireUser().id
	}
}
