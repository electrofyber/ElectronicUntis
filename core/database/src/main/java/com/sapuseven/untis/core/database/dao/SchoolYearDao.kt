package com.sapuseven.untis.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.sapuseven.untis.core.database.entity.SchoolYearEntity

@Dao
interface SchoolYearDao {
	@Query("SELECT * FROM SchoolYear WHERE userId = :userId")
	suspend fun getByUserId(userId: Long): List<SchoolYearEntity>
}
