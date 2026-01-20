package com.sapuseven.untis.core.domain.infocenter

import com.sapuseven.untis.core.domain.repository.DirectMessageRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.messages.DirectMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDirectMessagesUseCase @Inject constructor(
	private val userRepository: UserRepository,
	private val directMessageRepository: DirectMessageRepository,
) {
	private val currentUser = userRepository.getActiveUser()

	operator fun invoke(): Flow<Result<List<DirectMessage>>> =
		directMessageRepository.getMessages(currentUser)
			.map(Result.Companion::success)
			.catch { emit(Result.failure(it)) }

	operator fun invoke(id: Long): Flow<Result<DirectMessage>> =
		directMessageRepository.getMessage(currentUser, id)
			.map(Result.Companion::success)
			.catch { emit(Result.failure(it)) }
}
