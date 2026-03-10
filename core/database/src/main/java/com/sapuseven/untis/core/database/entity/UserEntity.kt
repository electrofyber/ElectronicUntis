package com.sapuseven.untis.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.sapuseven.untis.core.api.mobile.model.untis.SchoolInfo
import com.sapuseven.untis.core.api.mobile.model.untis.Settings
import com.sapuseven.untis.core.api.mobile.model.untis.UserData
import com.sapuseven.untis.core.model.timetable.TimeGrid

@Entity(
	tableName = "User",
)
data class UserEntity(
	@PrimaryKey(autoGenerate = true) val id: Long,
	val profileName: String = "",
	val apiHost: String, // When populated before schema version 12, this may be a full URL. Afterwards it acts as an override for the JsonRPC URL. TODO: Rename to apiUrlOverride and make nullable
	val schoolInfo: SchoolInfo? = null, // New with schema version 12
	@Deprecated(
		"Not populated with schema version 12",
		ReplaceWith("schoolInfo.schoolId")
	) val schoolId: String? = null,
	val user: String? = null,
	val key: String? = null,
	val anonymous: Boolean = false,
	val timeGrid: TimeGrid,
	val masterDataTimestamp: Long,
	val userData: UserData,
	val settings: Settings? = null,
	val created: Long? = null,
)

data class UserWithData(
	@Embedded val user: UserEntity,

	@Relation(parentColumn = "id", entityColumn = "userId") val absenceReasons: List<AbsenceReasonEntity>,

	@Relation(parentColumn = "id", entityColumn = "userId") val departments: List<DepartmentEntity>,

	@Relation(parentColumn = "id", entityColumn = "userId") val duties: List<DutyEntity>,

	@Relation(parentColumn = "id", entityColumn = "userId") val eventReasons: List<EventReasonEntity>,

	@Relation(parentColumn = "id", entityColumn = "userId") val eventReasonGroups: List<EventReasonGroupEntity>,

	@Relation(parentColumn = "id", entityColumn = "userId") val excuseStatuses: List<ExcuseStatusEntity>,

	@Relation(parentColumn = "id", entityColumn = "userId") val holidays: List<HolidayEntity>,

	@Relation(parentColumn = "id", entityColumn = "userId") val klassen: List<KlasseEntity>,

	@Relation(parentColumn = "id", entityColumn = "userId") val rooms: List<RoomEntity>,

	@Relation(parentColumn = "id", entityColumn = "userId") val subjects: List<SubjectEntity>,

	@Relation(parentColumn = "id", entityColumn = "userId") val teachers: List<TeacherEntity>,

	@Relation(parentColumn = "id", entityColumn = "userId") val teachingMethods: List<TeachingMethodEntity>,

	@Relation(parentColumn = "id", entityColumn = "userId") val schoolYears: List<SchoolYearEntity>,
)
