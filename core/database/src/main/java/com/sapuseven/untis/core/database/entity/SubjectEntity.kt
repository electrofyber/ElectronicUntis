package com.sapuseven.untis.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import com.sapuseven.untis.core.api.mobile.model.untis.masterdata.Subject
import com.sapuseven.untis.core.database.utils.EntityMapper
import com.sapuseven.untis.core.model.timetable.ElementType

@Entity(
	tableName = "Subject",
	primaryKeys = ["id", "userId"],
	indices = [Index("id"), Index("userId")],
	foreignKeys = [ForeignKey(
		entity = UserEntity::class,
		parentColumns = ["id"],
		childColumns = ["userId"],
		onDelete = ForeignKey.CASCADE
	)]
)
data class SubjectEntity(
	override val id: Long = 0,
	override val userId: Long = -1,
	override val name: String = "",
	val longName: String = "",
	val departmentIds: List<Long> = emptyList(),
	override val foreColor: String? = null,
	override val backColor: String? = null,
	override val active: Boolean = false,
	override val allowed: Boolean = true
) : ElementEntity(), Comparable<String> {
	companion object : EntityMapper<Subject, SubjectEntity> {
		override fun map(from: Subject, userId: Long) = SubjectEntity(
			id = from.id,
			userId = userId,
			name = from.name,
			longName = from.longName,
			departmentIds = from.departmentIds,
			foreColor = from.foreColor,
			backColor = from.backColor,
			active = from.active,
			allowed = from.displayAllowed,
		)
	}

	@Ignore
	override val type = ElementType.SUBJECT

	override fun compareTo(other: String) = if (
		name.contains(other, true)
		|| longName.contains(other, true)
	) 0 else name.compareTo(other)

	override fun getShortName(default: String) = name

	override fun getLongName(default: String) = longName
}
