package com.sapuseven.untis.core.domain.repository;

import com.sapuseven.untis.core.model.timetable.School;
import com.sapuseven.untis.core.model.user.UserCredentials;

interface LoginRepository {
	suspend fun getAppSharedSecret(
		apiUrl: String,
		username: String? = null,
		password: String? = null,
		secondFactor: String? = null
	): Result<String>

	suspend fun persistUser(
		existingUserId: Long? = null,
		displayName: String? = null,
		school: School,
		credentials: UserCredentials?,
	): Result<Long>
}
