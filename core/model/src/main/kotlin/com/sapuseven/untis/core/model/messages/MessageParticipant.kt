package com.sapuseven.untis.core.model.messages

import kotlinx.serialization.Serializable

@Serializable
sealed class MessageParticipant {
	abstract val id: Long
	abstract val name: String?
	abstract val avatarUrl: String?

	data class User(
		override val id: Long,
		override val name: String?,
		override val avatarUrl: String?
	) : MessageParticipant()

	data class Group(
		override val id: Long,
		override val name: String?,
		override val avatarUrl: String?,
	) : MessageParticipant()
}
