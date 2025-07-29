package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.database.entity.UserEntity
import com.sapuseven.untis.core.model.User

internal fun UserEntity.toDomain() = User(
	id = id,
	user = user,
	key = key,
	displayName = profileName,
	school = schoolInfo!!.toDomain(),
)

internal fun User.toEntity(): UserEntity = UserEntity(
	id = id,
	profileName = displayName,
	apiHost = school.apiUrl,
	schoolInfo = school.toEntity(),
	user = user,
	key = key,
	anonymous = anonymous,
	timeGrid = TODO(),
	masterDataTimestamp = TODO(),
	userData = TODO(),
)
