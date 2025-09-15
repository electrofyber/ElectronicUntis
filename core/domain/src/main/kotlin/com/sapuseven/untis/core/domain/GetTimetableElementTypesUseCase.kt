package com.sapuseven.untis.core.domain

import com.sapuseven.untis.core.data.repository.MasterDataRepository
import com.sapuseven.untis.core.model.ElementType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class GetTimetableElementTypesUseCase @Inject constructor(
	private val masterDataRepository: MasterDataRepository
) {
	operator fun invoke(): Flow<Set<ElementType>> {
		return masterDataRepository.timetableElements.map { it.keys }
	}
}
