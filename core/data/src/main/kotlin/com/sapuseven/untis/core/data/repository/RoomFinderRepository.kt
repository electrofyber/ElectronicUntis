package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.data.mapper.toEntity
import com.sapuseven.untis.core.database.entity.RoomFinderDao
import com.sapuseven.untis.core.database.entity.RoomFinderEntity
import com.sapuseven.untis.core.domain.repository.RoomFinderRepository
import com.sapuseven.untis.core.model.roomfinder.RoomFinderItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GenericRoomFinderRepository @Inject constructor(
	private val dao: RoomFinderDao
) : RoomFinderRepository {
	override fun observeAllRooms(userId: Long): Flow<List<RoomFinderItem>> =
		dao.getAllByUserId(userId)
			.map { entities -> entities.map(RoomFinderEntity::toDomain) }
			.distinctUntilChanged()

	override suspend fun insertAll(rooms: List<RoomFinderItem>) {
		dao.insertAll(rooms.map(RoomFinderItem::toEntity))
	}

	override suspend fun delete(room: RoomFinderItem) {
		dao.delete(room.toEntity())
	}
}
