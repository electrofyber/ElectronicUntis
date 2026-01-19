package com.sapuseven.untis.core.model.officehours

import kotlinx.serialization.Serializable

@Serializable
data class OfficeHourRegistrationInfo(
	val registrationPossible: Boolean,
	val registered: Boolean,
	val email: String?,
	val phone: String?,
)
