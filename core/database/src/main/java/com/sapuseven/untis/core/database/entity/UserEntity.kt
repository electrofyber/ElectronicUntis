package com.sapuseven.untis.core.database.entity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.sapuseven.untis.core.api.mobile.model.untis.MasterData
import com.sapuseven.untis.core.api.mobile.model.untis.SchoolInfo
import com.sapuseven.untis.core.api.mobile.model.untis.Settings
import com.sapuseven.untis.core.api.mobile.model.untis.UserData
import com.sapuseven.untis.core.model.timetable.TimeGrid
import kotlinx.coroutines.flow.Flow

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

@Dao
interface UserDao {
	@Query("SELECT * FROM user")
	fun getAllFlow(): Flow<List<UserEntity>>

	@Query("SELECT * FROM user")
	suspend fun getAllAsync(): List<UserEntity>

	@Deprecated("Should be migrated to getAllAsync() or getAllFlow()")
	@Query("SELECT * FROM user")
	fun getAll(): List<UserEntity>

	@Query("SELECT * FROM user WHERE id LIKE :userId")
	suspend fun getById(userId: Long): UserEntity?

	@Transaction
	@Query("SELECT * FROM user")
	suspend fun getAllWithData(): List<UserWithData>

	@Transaction
	@Query("SELECT * FROM user WHERE id LIKE :userId")
	suspend fun getByIdWithData(userId: Long): UserWithData?

	@Transaction
	@Query("SELECT * FROM user WHERE id LIKE :userId")
	fun getByIdWithDataFlow(userId: Long): Flow<UserWithData?>

	@Query("SELECT * FROM Klasse WHERE userId = :userId AND active = 1 ORDER BY name")
	fun getActiveClassesFlow(userId: Long): Flow<List<KlasseEntity>>

	@Query("SELECT * FROM Teacher WHERE userId = :userId AND active = 1 ORDER BY name")
	fun getActiveTeachersFlow(userId: Long): Flow<List<TeacherEntity>>

	@Query("SELECT * FROM Subject WHERE userId = :userId AND active = 1 ORDER BY name")
	fun getActiveSubjectsFlow(userId: Long): Flow<List<SubjectEntity>>

	@Query("SELECT * FROM Room WHERE userId = :userId AND active = 1 ORDER BY name")
	fun getActiveRoomsFlow(userId: Long): Flow<List<RoomEntity>>

	@Query("SELECT * FROM Klasse WHERE userId LIKE :userId AND id LIKE :id")
	suspend fun getClassById(userId: Long, id: Long): KlasseEntity?

	@Query("SELECT * FROM Teacher WHERE userId LIKE :userId AND id LIKE :id")
	suspend fun getTeacherById(userId: Long, id: Long): TeacherEntity?

	@Query("SELECT * FROM Subject WHERE userId LIKE :userId AND id LIKE :id")
	suspend fun getSubjectById(userId: Long, id: Long): SubjectEntity?

	@Query("SELECT * FROM Room WHERE userId LIKE :userId AND id LIKE :id")
	suspend fun getRoomById(userId: Long, id: Long): RoomEntity?

	@Insert
	suspend fun insert(user: UserEntity): Long

	@Insert
	suspend fun insertAbsenceReasons(absenceReasons: List<AbsenceReasonEntity>)

	@Insert
	suspend fun insertDepartments(departments: List<DepartmentEntity>)

	@Insert
	suspend fun insertDuties(duties: List<DutyEntity>)

	@Insert
	suspend fun insertEventReasons(eventReasons: List<EventReasonEntity>)

	@Insert
	suspend fun insertEventReasonGroups(eventReasonGroups: List<EventReasonGroupEntity>)

	@Insert
	suspend fun insertExcuseStatuses(excuseStatuses: List<ExcuseStatusEntity>)

	@Insert
	suspend fun insertHolidays(holidays: List<HolidayEntity>)

	@Insert
	suspend fun insertKlassen(klassen: List<KlasseEntity>)

	@Insert
	suspend fun insertRooms(rooms: List<RoomEntity>)

	@Insert
	suspend fun insertSubjects(subjects: List<SubjectEntity>)

	@Insert
	suspend fun insertTeachers(teachers: List<TeacherEntity>)

	@Insert
	suspend fun insertTeachingMethods(teachingMethods: List<TeachingMethodEntity>)

	@Insert
	suspend fun insertSchoolYears(schoolYears: List<SchoolYearEntity>)

	@Upsert
	suspend fun upsertAbsenceReasons(absenceReasons: List<AbsenceReasonEntity>)

	@Upsert
	suspend fun upsertDepartments(departments: List<DepartmentEntity>)

	@Upsert
	suspend fun upsertDuties(duties: List<DutyEntity>)

	@Upsert
	suspend fun upsertEventReasons(eventReasons: List<EventReasonEntity>)

	@Upsert
	suspend fun upsertEventReasonGroups(eventReasonGroups: List<EventReasonGroupEntity>)

	@Upsert
	suspend fun upsertExcuseStatuses(excuseStatuses: List<ExcuseStatusEntity>)

	@Upsert
	suspend fun upsertHolidays(holidays: List<HolidayEntity>)

	@Upsert
	suspend fun upsertKlassen(klassen: List<KlasseEntity>)

	@Upsert
	suspend fun upsertRooms(rooms: List<RoomEntity>)

	@Upsert
	suspend fun upsertSubjects(subjects: List<SubjectEntity>)

	@Upsert
	suspend fun upsertTeachers(teachers: List<TeacherEntity>)

	@Upsert
	suspend fun upsertTeachingMethods(teachingMethods: List<TeachingMethodEntity>)

	@Upsert
	suspend fun upsertSchoolYears(schoolYears: List<SchoolYearEntity>)

	@Transaction
	suspend fun insertMasterData(userId: Long, masterData: MasterData) {
		insertAbsenceReasons(masterData.absenceReasons.orEmpty().map { AbsenceReasonEntity.map(it, userId) })
		insertDepartments(masterData.departments.orEmpty().map { DepartmentEntity.map(it, userId) })
		insertDuties(masterData.duties.orEmpty().map { DutyEntity.map(it, userId) })
		insertEventReasons(masterData.eventReasons.orEmpty().map { EventReasonEntity.map(it, userId) })
		insertEventReasonGroups(masterData.eventReasonGroups.orEmpty().map { EventReasonGroupEntity.map(it, userId) })
		insertExcuseStatuses(masterData.excuseStatuses.orEmpty().map { ExcuseStatusEntity.map(it, userId) })
		insertHolidays(masterData.holidays.orEmpty().map { HolidayEntity.map(it, userId) })
		insertKlassen(masterData.klassen.map { KlasseEntity.map(it, userId) })
		insertRooms(masterData.rooms.map { RoomEntity.map(it, userId) })
		insertSubjects(masterData.subjects.map { SubjectEntity.map(it, userId) })
		insertTeachers(masterData.teachers.map { TeacherEntity.map(it, userId) })
		insertTeachingMethods(masterData.teachingMethods.orEmpty().map { TeachingMethodEntity.map(it, userId) })
		insertSchoolYears(masterData.schoolyears.orEmpty().map { SchoolYearEntity.map(it, userId) })
		updateMasterDataTimestamp(userId, masterData.timeStamp)
	}

	@Transaction
	suspend fun upsertMasterData(userId: Long, masterData: MasterData) {
		upsertAbsenceReasons(masterData.absenceReasons.orEmpty().map { AbsenceReasonEntity.map(it, userId) })
		upsertDepartments(masterData.departments.orEmpty().map { DepartmentEntity.map(it, userId) })
		upsertDuties(masterData.duties.orEmpty().map { DutyEntity.map(it, userId) })
		upsertEventReasons(masterData.eventReasons.orEmpty().map { EventReasonEntity.map(it, userId) })
		upsertEventReasonGroups(masterData.eventReasonGroups.orEmpty().map { EventReasonGroupEntity.map(it, userId) })
		upsertExcuseStatuses(masterData.excuseStatuses.orEmpty().map { ExcuseStatusEntity.map(it, userId) })
		upsertHolidays(masterData.holidays.orEmpty().map { HolidayEntity.map(it, userId) })
		upsertKlassen(masterData.klassen.map { KlasseEntity.map(it, userId) })
		upsertRooms(masterData.rooms.map { RoomEntity.map(it, userId) })
		upsertSubjects(masterData.subjects.map { SubjectEntity.map(it, userId) })
		upsertTeachers(masterData.teachers.map { TeacherEntity.map(it, userId) })
		upsertTeachingMethods(masterData.teachingMethods.orEmpty().map { TeachingMethodEntity.map(it, userId) })
		upsertSchoolYears(masterData.schoolyears.orEmpty().map { SchoolYearEntity.map(it, userId) })
		updateMasterDataTimestamp(userId, masterData.timeStamp)
	}

	@Query("UPDATE user SET masterDataTimestamp = :timestamp WHERE id = :userId")
	suspend fun updateMasterDataTimestamp(userId: Long, timestamp: Long)

	@Update
	suspend fun update(user: UserEntity)

	@Query("DELETE FROM User WHERE id = :userId")
	suspend fun delete(userId: Long)

	@Delete
	suspend fun deleteAbsenceReasons(absenceReasons: List<AbsenceReasonEntity>)

	@Delete
	suspend fun deleteDepartments(departments: List<DepartmentEntity>)

	@Delete
	suspend fun deleteDuties(duties: List<DutyEntity>)

	@Delete
	suspend fun deleteEventReasons(eventReasons: List<EventReasonEntity>)

	@Delete
	suspend fun deleteEventReasonGroups(eventReasonGroups: List<EventReasonGroupEntity>)

	@Delete
	suspend fun deleteExcuseStatuses(excuseStatuses: List<ExcuseStatusEntity>)

	@Delete
	suspend fun deleteHolidays(holidays: List<HolidayEntity>)

	@Delete
	suspend fun deleteKlassen(klassen: List<KlasseEntity>)

	@Delete
	suspend fun deleteRooms(rooms: List<RoomEntity>)

	@Delete
	suspend fun deleteSubjects(subjects: List<SubjectEntity>)

	@Delete
	suspend fun deleteTeachers(teachers: List<TeacherEntity>)

	@Delete
	suspend fun deleteTeachingMethods(teachingMethods: List<TeachingMethodEntity>)

	@Delete
	suspend fun deleteSchoolYears(schoolYears: List<SchoolYearEntity>)

	@Transaction
	suspend fun deleteMasterData(userWithData: UserWithData) {
		deleteAbsenceReasons(userWithData.absenceReasons)
		deleteDepartments(userWithData.departments)
		deleteDuties(userWithData.duties)
		deleteEventReasons(userWithData.eventReasons)
		deleteEventReasonGroups(userWithData.eventReasonGroups)
		deleteExcuseStatuses(userWithData.excuseStatuses)
		deleteHolidays(userWithData.holidays)
		deleteKlassen(userWithData.klassen)
		deleteRooms(userWithData.rooms)
		deleteSubjects(userWithData.subjects)
		deleteTeachers(userWithData.teachers)
		deleteTeachingMethods(userWithData.teachingMethods)
		deleteSchoolYears(userWithData.schoolYears)
	}

	@Transaction
	suspend fun deleteMasterData(userId: Long) = getByIdWithData(userId)?.let { deleteMasterData(it) }
}
