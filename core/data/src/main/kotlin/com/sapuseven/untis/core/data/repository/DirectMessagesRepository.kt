package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.api.rest.api.MessagesApi
import com.sapuseven.untis.core.api.rest.model.Message
import com.sapuseven.untis.core.api.rest.model.MessagesDraftsResponse
import com.sapuseven.untis.core.api.rest.model.MessagesResponse
import com.sapuseven.untis.core.api.rest.model.MessagesSentResponse
import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.domain.repository.DirectMessagesRepository
import com.sapuseven.untis.core.model.messages.DirectMessage
import com.sapuseven.untis.core.model.user.User
import crocodile8.universal_cache.time.TimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class UntisDirectMessagesRepository @Inject constructor(
	private val messagesApi: MessagesApi,
	@Named("cacheDir") cacheDir: File,
	timeProvider: TimeProvider,
) : BaseCachedRepository(cacheDir, timeProvider), DirectMessagesRepository {
	override fun getMessage(user: User, id: Long): Flow<DirectMessage> =
		cached<Long, Message>("messenger/message") { messagesApi.getMessage(it).body() }
			.invoke(id, user.id)
			.map(Message::toDomain)

	override fun getMessages(user: User): Flow<List<DirectMessage>> =
		cached<MessagesResponse>("messenger/messages") { messagesApi.getMessages().body() }
			.invoke(user.id)
			.map { result ->
				result.incomingMessages?.map { it.toDomain() } ?: emptyList()
			}

	override fun getMessagesSent(user: User): Flow<List<DirectMessage>> =
		cached<MessagesSentResponse>("messenger/messagesSent") { messagesApi.getMessagesSent().body() }
			.invoke(user.id)
			.map { result ->
				result.sentMessages?.map { it.toDomain() } ?: emptyList()
			}

	override fun getMessagesDrafts(user: User): Flow<List<DirectMessage>> =
		cached<MessagesDraftsResponse>("messenger/messagesDrafts") { messagesApi.getMessagesDrafts().body() }
			.invoke(user.id)
			.map { result ->
				result.draftMessages?.map { it.toDomain() } ?: emptyList()
			}
}
