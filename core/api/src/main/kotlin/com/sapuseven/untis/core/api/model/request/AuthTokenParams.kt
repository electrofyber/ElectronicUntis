package com.sapuseven.untis.core.api.model.request

import com.sapuseven.untis.core.api.model.untis.Auth
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenParams(
	val auth: Auth
) : BaseParams()
