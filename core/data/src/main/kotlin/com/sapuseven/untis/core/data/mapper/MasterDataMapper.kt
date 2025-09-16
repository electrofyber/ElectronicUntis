package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.model.masterdata.MasterData

internal fun com.sapuseven.untis.core.api.model.untis.MasterData.toDomain() = MasterData(
	timestamp = timeStamp,
)
