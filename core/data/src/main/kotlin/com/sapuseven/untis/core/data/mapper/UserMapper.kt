package com.sapuseven.untis.core.data.mapper

import androidx.core.net.toUri
import com.sapuseven.untis.core.api.mobile.model.untis.UserData
import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.ElementType
import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.Right
import com.sapuseven.untis.core.database.entity.UserEntity
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.School
import com.sapuseven.untis.core.model.user.User
import com.sapuseven.untis.core.model.user.UserCredentials
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// Map Entity -> Domain
internal fun UserEntity.toDomain() = User(
	id = id,
	name = userData.displayName,
	displayName = profileName.takeIf(String::isNotBlank) ?: userData.displayName,
	credentials = UserCredentials(user.orEmpty(), key.orEmpty()).takeIf { !anonymous },
	school = schoolInfo?.toDomain() ?: defaultSchoolInfo(),
	element = userData.elemType?.let { elementType ->
		Element.personal(
			id = userData.elemId,
			type = elementType.toDomain(),
			name = userData.displayName,
		)
	},
	rights = userData.rights.map(Right::toDomain),
	timeGrid = timeGrid
)

private fun UserEntity.defaultSchoolInfo(): School = School(
	name = schoolInfo?.loginName ?: apiHost.toUri().getQueryParameter("school") ?: "",
	displayName = userData.schoolName,
	address = null,
	apiUrl = apiHost
)

// Map Domain -> Entity
@OptIn(ExperimentalTime::class)
internal fun User.toEntity(): UserEntity = UserEntity(
	id = id,
	profileName = displayName,
	apiHost = school.apiUrl,
	schoolInfo = school.toEntity(),
	user = credentials?.user,
	key = credentials?.key,
	anonymous = isAnonymous,
	timeGrid = timeGrid,
	masterDataTimestamp = Clock.System.now().epochSeconds,
	userData = UserData(
		elemId = element?.id ?: -1,
		elemType = element?.type?.let { ElementType.valueOf(it.toString()) },
		displayName = name,
		schoolName = school.name,
		departmentId = -1L,
		children = emptyList(),
		klassenIds = emptyList(),
		rights = emptyList(),
	),
)
