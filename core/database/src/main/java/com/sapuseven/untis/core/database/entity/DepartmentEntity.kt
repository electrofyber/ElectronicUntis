package com.sapuseven.untis.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.sapuseven.untis.core.api.model.untis.masterdata.Department
import com.sapuseven.untis.core.database.utils.EntityMapper

@Entity(
	tableName = "Department",
	primaryKeys = ["id", "userId"],
	indices = [Index("id"), Index("userId")],
	foreignKeys = [ForeignKey(
		entity = UserEntity::class,
		parentColumns = ["id"],
		childColumns = ["userId"],
		onDelete = ForeignKey.CASCADE
	)]
)
data class DepartmentEntity(
	val id: Long,
	val userId: Long = -1,
	val name: String,
	val longName: String
) {
	companion object : EntityMapper<Department, DepartmentEntity> {
		override fun map(from: Department, userId: Long) = DepartmentEntity(
			id = from.id,
			userId = userId,
			name = from.name,
			longName = from.longName,
		)
	}
}
