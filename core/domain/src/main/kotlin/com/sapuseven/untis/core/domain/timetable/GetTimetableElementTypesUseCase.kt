package com.sapuseven.untis.core.domain.timetable

import com.sapuseven.untis.core.domain.repository.ElementRepository
import com.sapuseven.untis.core.model.timetable.ElementType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTimetableElementTypesUseCase @Inject constructor(
	private val elementRepository: ElementRepository
) {
	operator fun invoke(): Flow<Set<ElementType>> {
		return elementRepository.timetableElements.map { it.keys }
	}
}
