package com.sapuseven.untis.core.api.mobile.model.untis

import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.ElementType
import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.Right
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
	val elemId: Long,
	val elemType: ElementType?,
	val displayName: String,
	val schoolName: String,
	val departmentId: Long,
	val children: List<Person?> = emptyList(),
	val klassenIds: List<Long> = emptyList(),
	val rights: List<Right> = emptyList()
)
