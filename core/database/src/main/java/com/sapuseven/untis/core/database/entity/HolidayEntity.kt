package com.sapuseven.untis.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.sapuseven.untis.core.api.mobile.model.untis.masterdata.Holiday
import com.sapuseven.untis.core.database.utils.EntityMapper
import kotlinx.datetime.LocalDate

@Entity(
	tableName = "Holiday",
	primaryKeys = ["id", "userId"],
	indices = [Index("id"), Index("userId")],
	foreignKeys = [ForeignKey(
		entity = UserEntity::class,
		parentColumns = ["id"],
		childColumns = ["userId"],
		onDelete = ForeignKey.CASCADE
	)]
)
data class HolidayEntity(
	val id: Long = 0,
	val userId: Long = -1,
	val name: String = "",
	val longName: String = "",
	val startDate: LocalDate? = null,
	val endDate: LocalDate? = null
) {
	companion object : EntityMapper<Holiday, HolidayEntity> {
		override fun map(from: Holiday, userId: Long) = HolidayEntity(
			id = from.id,
			userId = userId,
			name = from.name,
			longName = from.longName,
			startDate = from.startDate,
			endDate = from.endDate,
		)
	}
}
