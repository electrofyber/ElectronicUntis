package com.sapuseven.untis.core.api.model.request

import kotlinx.serialization.Serializable

@Serializable
data class AppSharedSecretParams(
		val userName: String,
		val password: String,
		val token: String? = null,
) : BaseParams()
