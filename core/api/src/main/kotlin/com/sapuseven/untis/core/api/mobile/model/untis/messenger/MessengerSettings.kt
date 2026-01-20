package com.sapuseven.untis.core.api.mobile.model.untis.messenger

import kotlinx.serialization.Serializable

@Serializable
data class MessengerSettings(
	val serverUrl: String,
	val organizationId: String
)
