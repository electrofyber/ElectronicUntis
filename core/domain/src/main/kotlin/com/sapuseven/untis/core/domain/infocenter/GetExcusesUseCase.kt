package com.sapuseven.untis.core.domain.infocenter

import com.sapuseven.untis.core.model.absences.Excuse
import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetExcusesUseCase @Inject constructor() {
	// TODO
	operator fun invoke(user: User): Flow<List<Excuse>> = flowOf(emptyList())
}
