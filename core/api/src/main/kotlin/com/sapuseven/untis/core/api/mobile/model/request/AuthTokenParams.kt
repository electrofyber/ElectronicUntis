package com.sapuseven.untis.core.api.mobile.model.request

import com.sapuseven.untis.core.api.mobile.model.untis.Auth
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenParams(
	val auth: Auth
) : BaseParams()
