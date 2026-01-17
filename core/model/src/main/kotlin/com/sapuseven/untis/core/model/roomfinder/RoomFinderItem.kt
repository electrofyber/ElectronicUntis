package com.sapuseven.untis.core.model.roomfinder

import kotlinx.serialization.Serializable


@Serializable
data class RoomFinderItem(
	val elementId: Long,
	val userId: Long,
)
