package com.sapuseven.untis.core.domain.repository

import com.sapuseven.untis.core.model.roomfinder.RoomFinderItem
import kotlinx.coroutines.flow.Flow

interface RoomFinderRepository {
	fun observeAllRooms(userId: Long): Flow<List<RoomFinderItem>>

	suspend fun insertAll(rooms: List<RoomFinderItem>)

	suspend fun delete(room: RoomFinderItem)
}
