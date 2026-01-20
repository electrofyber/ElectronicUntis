package com.sapuseven.untis.core.api.mobile.model.request

import com.sapuseven.untis.core.api.mobile.model.untis.Auth
import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.DeviceOs
import kotlinx.serialization.Serializable

@Serializable
data class UserDataParams(
	val elementId: Int = 0,
	val deviceOs: DeviceOs = DeviceOs.AND,
	val deviceOsVersion: String = "",
	val auth: Auth?
) : BaseParams()
