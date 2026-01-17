package com.sapuseven.untis.core.database.entity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sapuseven.untis.core.database.utils.EntityMapper
import com.sapuseven.untis.core.model.roomfinder.RoomFinderItemData
import kotlinx.coroutines.flow.Flow

@Entity(
	tableName = "rooms",
	primaryKeys = ["id", "userId"],
	indices = [Index("id"), Index("userId")]
)
data class RoomFinderEntity(
	val id: Long,
	val userId: Long
) {
	companion object : EntityMapper<RoomFinderItemData, RoomFinderEntity> {
		override fun map(from: RoomFinderItemData, userId: Long) = RoomFinderEntity(
			id = from.room.elementId,
			userId = userId,
		)
	}
}

@Dao
interface RoomFinderDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(roomFinderEntities: List<RoomFinderEntity>)

	@Delete
	suspend fun delete(roomFinderEntity: RoomFinderEntity)

	@Query("SELECT * FROM rooms WHERE userId LIKE :userId")
	fun getAllByUserId(userId: Long): Flow<List<RoomFinderEntity>>
}
