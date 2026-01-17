package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.database.entity.RoomFinderEntity
import com.sapuseven.untis.core.model.roomfinder.RoomFinderItem
import kotlin.time.ExperimentalTime


// Map Entity -> Domain
internal fun RoomFinderEntity.toDomain() = RoomFinderItem(
	this.id, this.userId
)

// Map Domain -> Entity
@OptIn(ExperimentalTime::class)
internal fun RoomFinderItem.toEntity(): RoomFinderEntity = RoomFinderEntity(
	this.elementId, this.userId
)
