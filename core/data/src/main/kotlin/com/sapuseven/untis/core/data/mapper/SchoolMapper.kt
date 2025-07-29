package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.model.untis.SchoolInfo
import com.sapuseven.untis.core.database.entity.UserEntity
import com.sapuseven.untis.core.database.entity.UserEntity.Companion.buildApiUrl
import com.sapuseven.untis.core.model.School

internal fun SchoolInfo.toDomain() = School(
	name = loginName,
	displayName = displayName,
	apiUrl = UserEntity.buildJsonRpcApiUrl(
		buildApiUrl("", this),
		loginName
	).toString()
)

internal fun School.toEntity(): SchoolInfo = SchoolInfo(
	server = apiUrl,
	useMobileServiceUrlAndroid = false,
	useMobileServiceUrlIos = false,
	address = "",
	displayName = displayName,
	loginName = name,
	schoolId = 0L, // This is not used in the app, so we can set it to 0
	tenantId = null, // Not used in the app
	serverUrl = apiUrl,
	mobileServiceUrl = null // Not used in the app
)
