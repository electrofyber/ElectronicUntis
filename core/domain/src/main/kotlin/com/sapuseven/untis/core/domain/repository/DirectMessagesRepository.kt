package com.sapuseven.untis.core.domain.repository

import com.sapuseven.untis.core.model.messages.DirectMessage
import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.flow.Flow

interface DirectMessagesRepository {
	fun getMessage(user: User, id: Long): Flow<DirectMessage>

	fun getMessages(user: User): Flow<List<DirectMessage>>

	fun getMessagesSent(user: User): Flow<List<DirectMessage>>

	fun getMessagesDrafts(user: User): Flow<List<DirectMessage>>
}
