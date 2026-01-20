package com.sapuseven.untis.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import com.sapuseven.untis.core.api.mobile.model.untis.masterdata.Teacher
import com.sapuseven.untis.core.database.utils.EntityMapper
import com.sapuseven.untis.core.model.timetable.ElementType
import kotlinx.datetime.LocalDate

@Entity(
	tableName = "Teacher",
	primaryKeys = ["id", "userId"],
	indices = [Index("id"), Index("userId")],
	foreignKeys = [ForeignKey(
		entity = UserEntity::class,
		parentColumns = ["id"],
		childColumns = ["userId"],
		onDelete = ForeignKey.CASCADE
	)]
)
data class TeacherEntity(
	override val id: Long = 0,
	override val userId: Long = -1,
	override val name: String = "",
	val firstName: String = "",
	val lastName: String = "",
	val departmentIds: List<Long> = emptyList(),
	override val foreColor: String? = null,
	override val backColor: String? = null,
	val entryDate: LocalDate? = null,
	val exitDate: LocalDate? = null,
	override val active: Boolean = false,
	override val allowed: Boolean = true
) : ElementEntity(), Comparable<String> {
	companion object : EntityMapper<Teacher, TeacherEntity> {
		override fun map(from: Teacher, userId: Long) = TeacherEntity(
			id = from.id,
			userId = userId,
			name = from.name,
			firstName = from.firstName,
			lastName = from.lastName,
			departmentIds = from.departmentIds,
			foreColor = from.foreColor,
			backColor = from.backColor,
			entryDate = from.entryDate,
			exitDate = from.exitDate,
			active = from.active,
			allowed = from.displayAllowed,
		)
	}

	@Ignore
	override val type = ElementType.TEACHER

	override fun compareTo(other: String) = if (
		name.contains(other, true)
		|| firstName.contains(other, true)
		|| lastName.contains(other, true)
	) 0 else name.compareTo(other)

	override fun getShortName(default: String) = name

	override fun getLongName(default: String) = "$firstName $lastName"
}
