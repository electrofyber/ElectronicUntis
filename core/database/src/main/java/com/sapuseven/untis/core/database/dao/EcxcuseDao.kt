package com.sapuseven.untis.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.sapuseven.untis.core.database.entity.ExcuseStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExcuseDao {
	@Query("SELECT * FROM ExcuseStatus WHERE userId = :userId")
	fun getByUserId(userId: Long): Flow<List<ExcuseStatusEntity>>
}
