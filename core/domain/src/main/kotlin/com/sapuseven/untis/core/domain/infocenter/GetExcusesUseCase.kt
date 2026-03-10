package com.sapuseven.untis.core.domain.infocenter

import com.sapuseven.untis.core.domain.repository.InfoCenterRepository
import com.sapuseven.untis.core.model.absences.Excuse
import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExcusesUseCase @Inject constructor(
	private val infoCenterRepository: InfoCenterRepository,
) {
	operator fun invoke(user: User): Flow<List<Excuse>> = infoCenterRepository.getExcuses(user)
}
